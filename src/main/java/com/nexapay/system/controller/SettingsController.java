package com.nexapay.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexapay.system.entity.ApiKey;
import com.nexapay.system.entity.User;
import com.nexapay.system.mapper.ApiKeyMapper;
import com.nexapay.system.mapper.UserMapper;
import com.nexapay.system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 设置控制器
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final ApiKeyMapper apiKeyMapper;
    private final UserMapper userMapper;
    private final AuthService authService;

    /**
     * 获取用户的 API Keys
     */
    @GetMapping("/api-keys")
    public ResponseEntity<List<ApiKey>> getApiKeys(
            @RequestHeader("Authorization") String token) {
        User user = authService.verifyToken(token.replace("Bearer ", ""));
        if (user == null) return ResponseEntity.status(401).build();

        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getTenantId, user.getTenantId())
               .eq(ApiKey::getStatus, "ACTIVE")
               .orderByDesc(ApiKey::getCreatedAt);
        return ResponseEntity.ok(apiKeyMapper.selectList(wrapper));
    }

    /**
     * 生成新的 API Key
     */
    @PostMapping("/api-keys")
    public ResponseEntity<Map<String, Object>> generateApiKey(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {
        User user = authService.verifyToken(token.replace("Bearer ", ""));
        if (user == null) return ResponseEntity.status(401).build();

        String name = body.getOrDefault("name", "New Key");
        String permissions = body.getOrDefault("permissions", "READ");

        ApiKey apiKey = new ApiKey();
        apiKey.setUuid(UUID.randomUUID());
        apiKey.setTenantId(user.getTenantId());
        apiKey.setUserId(user.getId());
        apiKey.setName(name);
        // Generate a fake key for display
        String rawKey = "sk_live_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        apiKey.setKeyHash(rawKey.substring(0, 12) + "••••••••••••" + rawKey.substring(rawKey.length() - 4));
        apiKey.setPermissions(permissions);
        apiKey.setStatus("ACTIVE");
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKeyMapper.insert(apiKey);

        return ResponseEntity.ok(Map.of(
            "id", apiKey.getId(),
            "name", apiKey.getName(),
            "key", rawKey,  // Return full key only once
            "keyHash", apiKey.getKeyHash(),
            "created", apiKey.getCreatedAt()
        ));
    }

    /**
     * 删除 API Key
     */
    @DeleteMapping("/api-keys/{keyId}")
    public ResponseEntity<Map<String, Object>> deleteApiKey(
            @RequestHeader("Authorization") String token,
            @PathVariable Long keyId) {
        User user = authService.verifyToken(token.replace("Bearer ", ""));
        if (user == null) return ResponseEntity.status(401).build();

        ApiKey key = apiKeyMapper.selectById(keyId);
        if (key == null || !key.getTenantId().equals(user.getTenantId())) {
            return ResponseEntity.notFound().build();
        }
        apiKeyMapper.deleteById(keyId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * 更新用户 Profile
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {
        User user = authService.verifyToken(token.replace("Bearer ", ""));
        if (user == null) return ResponseEntity.status(401).build();

        if (body.containsKey("email")) user.setEmail(body.get("email"));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        return ResponseEntity.ok(Map.of("success", true, "email", user.getEmail()));
    }
}
