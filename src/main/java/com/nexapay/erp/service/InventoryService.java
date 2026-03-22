package com.nexapay.erp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.erp.entity.Inventory;
import com.nexapay.erp.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库存服务
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryMapper inventoryMapper;

    /**
     * 获取库存列表
     */
    public Page<Inventory> getInventoryList(Long tenantId, int page, int size, String category) {
        Page<Inventory> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getTenantId, tenantId);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Inventory::getCategory, category);
        }
        wrapper.orderByDesc(Inventory::getCreatedAt);
        return inventoryMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取低库存商品
     */
    public List<Inventory> getLowStockItems(Long tenantId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getTenantId, tenantId)
               .eq(Inventory::getStatus, "ACTIVE")
               .apply("quantity <= low_stock_threshold");
        return inventoryMapper.selectList(wrapper);
    }

    /**
     * 更新库存数量
     */
    public boolean updateQuantity(Long tenantId, Long inventoryId, int quantity) {
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory != null && inventory.getTenantId().equals(tenantId)) {
            inventory.setQuantity(quantity);
            inventory.setUpdatedAt(LocalDateTime.now());
            return inventoryMapper.updateById(inventory) > 0;
        }
        return false;
    }

    /**
     * 创建库存商品
     */
    public Inventory createInventory(Inventory inventory) {
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryMapper.insert(inventory);
        return inventory;
    }

    /**
     * 获取库存统计
     */
    public Map<String, Object> getInventoryStats(Long tenantId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getTenantId, tenantId);

        List<Inventory> items = inventoryMapper.selectList(wrapper);

        long total = items.size();
        long active = items.stream().filter(i -> "ACTIVE".equals(i.getStatus())).count();
        long lowStock = items.stream()
            .filter(i -> i.getQuantity() <= i.getLowStockThreshold())
            .count();

        return Map.of(
            "total", total,
            "active", active,
            "lowStock", lowStock
        );
    }
}
