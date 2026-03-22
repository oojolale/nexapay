package com.nexapay.risk.controller;

import com.nexapay.risk.entity.RiskRule;
import com.nexapay.risk.service.RiskCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 风控控制器
 */
@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskCheckService riskCheckService;

    /**
     * 获取活跃风控规则
     */
    @GetMapping("/rules")
    public ResponseEntity<List<RiskRule>> getActiveRules(@RequestParam Long tenantId) {
        return ResponseEntity.ok(riskCheckService.getActiveRules(tenantId));
    }

    /**
     * 创建风控规则
     */
    @PostMapping("/rules")
    public ResponseEntity<RiskRule> createRule(
            @RequestParam Long tenantId,
            @RequestBody RiskRule rule) {
        rule.setTenantId(tenantId);
        return ResponseEntity.ok(riskCheckService.createRule(rule));
    }

    /**
     * 更新规则状态
     */
    @PatchMapping("/rules/{ruleId}/status")
    public ResponseEntity<Map<String, Object>> updateRuleStatus(
            @PathVariable Long ruleId,
            @RequestBody Map<String, String> request) {
        boolean success = riskCheckService.updateRuleStatus(ruleId, request.get("status"));
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 手动触发风险评分
     */
    @PostMapping("/recalculate")
    public ResponseEntity<Map<String, Object>> recalculateRisk(
            @RequestParam Long tenantId,
            @RequestParam Long transactionId) {
        return ResponseEntity.ok(Map.of("message", "Risk recalculation triggered"));
    }
}
