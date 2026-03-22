package com.nexapay.system.controller;

import com.nexapay.system.entity.Tenant;
import com.nexapay.system.entity.User;
import com.nexapay.system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 获取租户列表
     */
    @GetMapping("/tenants")
    public ResponseEntity<List<Tenant>> getTenants() {
        return ResponseEntity.ok(authService.getTenants());
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        Long tenantId = Long.parseLong(request.get("tenantId"));

        User user = authService.login(username, password, tenantId);
        if (user != null) {
            String token = authService.createToken(user);
            return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "role", user.getRole(),
                    "tenantId", user.getTenantId()
                )
            ));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    /**
     * 验证Token
     */
    @GetMapping("/verify")
    public ResponseEntity<User> verify(@RequestHeader("Authorization") String token) {
        User user = authService.verifyToken(token.replace("Bearer ", ""));
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }

    /**
     * 获取租户信息
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Tenant> getTenant(@PathVariable Long tenantId) {
        Tenant tenant = authService.getTenantById(tenantId);
        if (tenant != null) {
            return ResponseEntity.ok(tenant);
        }
        return ResponseEntity.notFound().build();
    }
}
