package com.nexapay.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexapay.payment.entity.Transaction;
import com.nexapay.risk.entity.RiskRule;
import com.nexapay.risk.mapper.RiskRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 风控检查服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskCheckService {

    private final RiskRuleMapper riskRuleMapper;

    /**
     * 计算交易风险分数
     */
    public int calculateRiskScore(Transaction transaction) {
        int score = 0;
        
        // Get active rules
        LambdaQueryWrapper<RiskRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RiskRule::getTenantId, transaction.getTenantId())
               .eq(RiskRule::getStatus, "ACTIVE")
               .orderByDesc(RiskRule::getPriority);
        
        List<RiskRule> rules = riskRuleMapper.selectList(wrapper);
        
        for (RiskRule rule : rules) {
            int ruleScore = evaluateRule(rule, transaction);
            score = Math.max(score, ruleScore);
            if (score >= 100) break;
        }
        
        return Math.min(score, 100);
    }

    /**
     * 评估单条规则
     */
    private int evaluateRule(RiskRule rule, Transaction transaction) {
        try {
            switch (rule.getRuleType()) {
                case "AMOUNT":
                    return evaluateAmountRule(rule, transaction);
                case "COUNTRY":
                    return evaluateCountryRule(rule, transaction);
                case "VELOCITY":
                    return evaluateVelocityRule(rule, transaction);
                default:
                    return 0;
            }
        } catch (Exception e) {
            log.error("Error evaluating rule: {}", rule.getName(), e);
            return 0;
        }
    }

    private int evaluateAmountRule(RiskRule rule, Transaction transaction) {
        // Simplified: check if amount > threshold
        if (transaction.getAmount() != null && transaction.getAmount().compareTo(new java.math.BigDecimal("5000")) > 0) {
            return 60;
        }
        return 0;
    }

    private int evaluateCountryRule(RiskRule rule, Transaction transaction) {
        // Simplified: block crypto payments for high risk
        if ("CRYPTO".equals(transaction.getPaymentMethod())) {
            return 80;
        }
        return 0;
    }

    private int evaluateVelocityRule(RiskRule rule, Transaction transaction) {
        // Simplified: check transaction frequency
        return 0;
    }

    /**
     * 获取活跃规则列表
     */
    public List<RiskRule> getActiveRules(Long tenantId) {
        LambdaQueryWrapper<RiskRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RiskRule::getTenantId, tenantId)
               .eq(RiskRule::getStatus, "ACTIVE")
               .orderByDesc(RiskRule::getPriority);
        return riskRuleMapper.selectList(wrapper);
    }

    /**
     * 创建风控规则
     */
    public RiskRule createRule(RiskRule rule) {
        riskRuleMapper.insert(rule);
        return rule;
    }

    /**
     * 更新规则状态
     */
    public boolean updateRuleStatus(Long ruleId, String status) {
        RiskRule rule = riskRuleMapper.selectById(ruleId);
        if (rule != null) {
            rule.setStatus(status);
            return riskRuleMapper.updateById(rule) > 0;
        }
        return false;
    }
}
