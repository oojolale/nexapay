package com.nexapay.erp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.erp.entity.Order;
import com.nexapay.erp.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;

    /**
     * 获取租户订单列表
     */
    public Page<Order> getOrders(Long tenantId, int page, int size, String status) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getTenantId, tenantId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        return orderMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取订单详情
     */
    public Order getOrderById(Long tenantId, Long orderId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getTenantId, tenantId)
               .eq(Order::getId, orderId);
        return orderMapper.selectOne(wrapper);
    }

    /**
     * 创建订单
     */
    public Order createOrder(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.insert(order);
        return order;
    }

    /**
     * 更新订单状态
     */
    public boolean updateOrderStatus(Long tenantId, Long orderId, String status) {
        Order order = getOrderById(tenantId, orderId);
        if (order != null) {
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            return orderMapper.updateById(order) > 0;
        }
        return false;
    }

    /**
     * 获取订单统计
     */
    public Map<String, Object> getOrderStats(Long tenantId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getTenantId, tenantId);

        List<Order> orders = orderMapper.selectList(wrapper);

        long total = orders.size();
        long completed = orders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count();
        long pending = orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long cancelled = orders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();

        BigDecimal totalAmount = orders.stream()
            .map(Order::getAmount)
            .filter(a -> a != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
            "total", total,
            "completed", completed,
            "pending", pending,
            "cancelled", cancelled,
            "totalAmount", totalAmount
        );
    }
}
