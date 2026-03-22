package com.nexapay.payment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.payment.entity.Transaction;
import com.nexapay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 获取交易列表
     */
    @GetMapping
    public ResponseEntity<Page<Transaction>> getTransactions(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(paymentService.getTransactions(tenantId, page, size, status));
    }

    /**
     * 创建交易
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestParam Long tenantId,
            @RequestBody Transaction transaction) {
        transaction.setTenantId(tenantId);
        return ResponseEntity.ok(paymentService.createTransaction(transaction));
    }

    /**
     * 更新交易状态
     */
    @PatchMapping("/{transactionId}/status")
    public ResponseEntity<?> updateStatus(
            @RequestParam Long tenantId,
            @PathVariable Long transactionId,
            @RequestBody Transaction request) {
        boolean success = paymentService.updateTransactionStatus(tenantId, transactionId, request.getStatus());
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 获取支付统计
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam Long tenantId) {
        return ResponseEntity.ok(paymentService.getPaymentStats(tenantId));
    }
}
