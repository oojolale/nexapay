package com.nexapay.erp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.erp.entity.Order;
import com.nexapay.erp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/erp/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 获取订单列表
     */
    @GetMapping
    public ResponseEntity<Page<Order>> getOrders(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.getOrders(tenantId, page, size, status));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @RequestParam Long tenantId,
            @PathVariable Long orderId) {
        Order order = orderService.getOrderById(tenantId, orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建订单
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam Long tenantId,
            @RequestBody Order order) {
        order.setTenantId(tenantId);
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    /**
     * 更新订单状态
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @RequestParam Long tenantId,
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        boolean success = orderService.updateOrderStatus(tenantId, orderId, status);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@RequestParam Long tenantId) {
        return ResponseEntity.ok(orderService.getOrderStats(tenantId));
    }
}
