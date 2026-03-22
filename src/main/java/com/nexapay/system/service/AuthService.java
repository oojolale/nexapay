package com.nexapay.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexapay.system.entity.User;
import com.nexapay.system.entity.Tenant;
import com.nexapay.system.mapper.UserMapper;
import com.nexapay.system.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final TenantMapper tenantMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String JWT_PREFIX = "nexapay:jwt:";
    private static final String TOTP_PREFIX = "nexapay:totp:";

    /**
     * 用户登录
     */
    public User login(String username, String password, Long tenantId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getTenantId, tenantId)
               .eq(User::getStatus, "ACTIVE");
        
        User user = userMapper.selectOne(wrapper);
        if (user != null && verifyPassword(password, user.getPasswordHash())) {
            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            userMapper.updateById(user);
            return user;
        }
        return null;
    }

    /**
     * 验证密码（简化版本，生产环境使用BCrypt）
     */
    private boolean verifyPassword(String rawPassword, String passwordHash) {
        // Demo: password is "admin123" for all users
        return rawPassword.equals("admin123") || rawPassword.equals(passwordHash);
    }

    /**
     * 创建JWT Token
     */
    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        String key = JWT_PREFIX + token;
        redisTemplate.opsForValue().set(key, user, 24, TimeUnit.HOURS);
        return token;
    }

    /**
     * 验证Token
     */
    public User verifyToken(String token) {
        String key = JWT_PREFIX + token;
        Object obj = redisTemplate.opsForValue().get(key);
        
        if (obj instanceof User) {
            return (User) obj;
        } else if (obj != null) {
            // 如果反序列化失败，返回null
            return null;
        }
        return null;
    }

    /**
     * 获取租户列表
     */
    public List<Tenant> getTenants() {
        return tenantMapper.selectList(null);
    }

    /**
     * 根据ID获取租户
     */
    public Tenant getTenantById(Long tenantId) {
        return tenantMapper.selectById(tenantId);
    }

    /**
     * 生成TOTP密钥
     */
    public String generateTotpSecret(String username) {
        String secret = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        redisTemplate.opsForValue().set(TOTP_PREFIX + username, secret, 5, TimeUnit.MINUTES);
        return secret;
    }

    /**
     * 验证TOTP
     */
    public boolean verifyTotp(String username, String code) {
        String key = TOTP_PREFIX + username;
        String secret = (String) redisTemplate.opsForValue().get(key);
        return secret != null && secret.equals(code);
    }
}
