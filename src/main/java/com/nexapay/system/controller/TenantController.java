package com.nexapay.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexapay.system.entity.Tenant;
import com.nexapay.system.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 租户管理控制器
 */
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantMapper tenantMapper;

    /**
     * 获取所有租户列表（管理）
     */
    @GetMapping
    public ResponseEntity<List<Tenant>> listTenants(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String plan) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Tenant::getStatus, status);
        }
        if (plan != null && !plan.isEmpty()) {
            wrapper.eq(Tenant::getPlan, plan);
        }
        wrapper.orderByDesc(Tenant::getCreatedAt);
        return ResponseEntity.ok(tenantMapper.selectList(wrapper));
    }

    /**
     * 获取租户统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Tenant> all = tenantMapper.selectList(null);
        long total = all.size();
        long active = all.stream().filter(t -> "ACTIVE".equals(t.getStatus())).count();
        long suspended = all.stream().filter(t -> "SUSPENDED".equals(t.getStatus())).count();
        return ResponseEntity.ok(Map.of(
            "total", total,
            "active", active,
            "suspended", suspended
        ));
    }

    /**
     * 创建租户
     */
    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody Tenant tenant) {
        tenant.setUuid(UUID.randomUUID());
        tenant.setStatus("ACTIVE");
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.insert(tenant);
        return ResponseEntity.ok(tenant);
    }

    /**
     * 更新租户状态
     */
    @PatchMapping("/{tenantId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long tenantId,
            @RequestBody Map<String, String> body) {
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            return ResponseEntity.notFound().build();
        }
        tenant.setStatus(body.get("status"));
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.updateById(tenant);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * 更新租户信息
     */
    @PutMapping("/{tenantId}")
    public ResponseEntity<Tenant> updateTenant(
            @PathVariable Long tenantId,
            @RequestBody Tenant body) {
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            return ResponseEntity.notFound().build();
        }
        if (body.getName() != null) tenant.setName(body.getName());
        if (body.getDomain() != null) tenant.setDomain(body.getDomain());
        if (body.getPlan() != null) tenant.setPlan(body.getPlan());
        if (body.getMaxUsers() != null) tenant.setMaxUsers(body.getMaxUsers());
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.updateById(tenant);
        return ResponseEntity.ok(tenant);
    }

    /**
     * 删除租户（软删除）
     */
    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Map<String, Object>> deleteTenant(@PathVariable Long tenantId) {
        tenantMapper.deleteById(tenantId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
