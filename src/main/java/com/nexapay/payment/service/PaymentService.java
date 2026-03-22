package com.nexapay.payment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.payment.entity.Transaction;
import com.nexapay.payment.mapper.TransactionMapper;
import com.nexapay.risk.service.RiskCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 支付服务
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionMapper transactionMapper;
    private final RiskCheckService riskCheckService;

    /**
     * 获取交易列表
     */
    public Page<Transaction> getTransactions(Long tenantId, int page, int size, String status) {
        Page<Transaction> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getTenantId, tenantId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Transaction::getStatus, status);
        }
        wrapper.orderByDesc(Transaction::getCreatedAt);
        return transactionMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 创建交易
     */
    public Transaction createTransaction(Transaction transaction) {
        // Generate transaction ID
        transaction.setTransactionId("TXN-" + System.currentTimeMillis());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        
        // Risk check
        int riskScore = riskCheckService.calculateRiskScore(transaction);
        transaction.setRiskScore(riskScore);
        
        if (riskScore >= 80) {
            transaction.setRiskStatus("BLOCKED");
            transaction.setStatus("BLOCKED");
        } else if (riskScore >= 50) {
            transaction.setRiskStatus("REVIEW");
            transaction.setStatus("PENDING_REVIEW");
        } else {
            transaction.setRiskStatus("PASSED");
            transaction.setStatus("PENDING");
        }
        
        transactionMapper.insert(transaction);
        return transaction;
    }

    /**
     * 更新交易状态
     */
    public boolean updateTransactionStatus(Long tenantId, Long transactionId, String status) {
        Transaction transaction = transactionMapper.selectById(transactionId);
        if (transaction != null && transaction.getTenantId().equals(tenantId)) {
            transaction.setStatus(status);
            transaction.setUpdatedAt(LocalDateTime.now());
            return transactionMapper.updateById(transaction) > 0;
        }
        return false;
    }

    /**
     * 获取支付统计
     */
    public Map<String, Object> getPaymentStats(Long tenantId) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getTenantId, tenantId);

        List<Transaction> transactions = transactionMapper.selectList(wrapper);

        long total = transactions.size();
        long completed = transactions.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
        long pending = transactions.stream().filter(t -> "PENDING".equals(t.getStatus())).count();
        long blocked = transactions.stream().filter(t -> "BLOCKED".equals(t.getStatus())).count();

        BigDecimal totalAmount = transactions.stream()
            .filter(t -> "COMPLETED".equals(t.getStatus()))
            .map(Transaction::getAmount)
            .filter(a -> a != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
            "total", total,
            "completed", completed,
            "pending", pending,
            "blocked", blocked,
            "totalAmount", totalAmount
        );
    }
}
