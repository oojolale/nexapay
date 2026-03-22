package com.nexapay.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexapay.payment.entity.Transaction;
import com.nexapay.payment.mapper.TransactionMapper;
import com.nexapay.risk.entity.RiskRule;
import com.nexapay.risk.mapper.RiskRuleMapper;
import com.nexapay.scheduler.entity.ScheduledTask;
import com.nexapay.scheduler.mapper.ScheduledTaskMapper;
import com.nexapay.system.entity.Tenant;
import com.nexapay.system.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TenantMapper tenantMapper;
    private final TransactionMapper transactionMapper;
    private final RiskRuleMapper riskRuleMapper;
    private final ScheduledTaskMapper scheduledTaskMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取仪表盘概览统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // Tenant stats
        List<Tenant> tenants = tenantMapper.selectList(null);
        long activeTenants = tenants.stream().filter(t -> "ACTIVE".equals(t.getStatus())).count();
        stats.put("activeTenants", activeTenants);
        stats.put("totalTenants", tenants.size());

        // Transaction volume
        LambdaQueryWrapper<Transaction> txWrapper = new LambdaQueryWrapper<>();
        txWrapper.eq(Transaction::getStatus, "COMPLETED");
        List<Transaction> completedTxns = transactionMapper.selectList(txWrapper);
        BigDecimal totalVolume = completedTxns.stream()
            .map(Transaction::getAmount)
            .filter(a -> a != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("transactionVolume", totalVolume);

        // Risk alerts
        LambdaQueryWrapper<Transaction> riskWrapper = new LambdaQueryWrapper<>();
        riskWrapper.ge(Transaction::getRiskScore, 60);
        long riskAlerts = transactionMapper.selectCount(riskWrapper);
        stats.put("riskAlerts", riskAlerts);

        // System uptime (mock)
        stats.put("systemUptime", "99.97%");

        return ResponseEntity.ok(stats);
    }

    /**
     * 获取租户资源使用情况（用于 Dashboard 图表）
     */
    @GetMapping("/tenant-usage")
    public ResponseEntity<List<Map<String, Object>>> getTenantUsage() {
        List<Tenant> tenants = tenantMapper.selectList(null);
        List<Map<String, Object>> usage = new ArrayList<>();

        for (Tenant tenant : tenants) {
            Map<String, Object> item = new HashMap<>();
            item.put("tenantId", tenant.getId());
            item.put("tenantName", tenant.getName());
            // Mock usage percentages
            int apiCallPct = (int)(Math.random() * 60 + 20);
            item.put("apiCallsPercent", apiCallPct);
            item.put("storagePercent", (int)(Math.random() * 60 + 20));
            item.put("usersPercent", (int)(Math.random() * 60 + 20));
            usage.add(item);
        }

        return ResponseEntity.ok(usage);
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/system-health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        // Redis info (mock)
        try {
            Long redisConnections = redisTemplate.getConnectionFactory() != null ? 1247L : 0L;
            health.put("redisConnections", redisConnections);
        } catch (Exception e) {
            health.put("redisConnections", 0);
        }

        // Mock system metrics
        health.put("cpuUsage", 34);
        health.put("memoryUsage", 62);
        health.put("activeLocks", 89);
        health.put("queueDepth", 342);
        health.put("uptime", "99.97%");

        return ResponseEntity.ok(health);
    }

    /**
     * 获取最近活动
     */
    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity() {
        List<Map<String, Object>> activities = new ArrayList<>();

        // Recent transactions
        LambdaQueryWrapper<Transaction> txWrapper = new LambdaQueryWrapper<>();
        txWrapper.orderByDesc(Transaction::getCreatedAt).last("LIMIT 5");
        List<Transaction> txns = transactionMapper.selectList(txWrapper);
        for (Transaction tx : txns) {
            Map<String, Object> a = new HashMap<>();
            a.put("type", "payment");
            a.put("text", "Payment processed — $" + tx.getAmount());
            a.put("time", tx.getCreatedAt());
            activities.add(a);
        }

        return ResponseEntity.ok(activities);
    }
}
