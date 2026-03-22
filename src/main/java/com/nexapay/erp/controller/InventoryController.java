package com.nexapay.erp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.erp.entity.Inventory;
import com.nexapay.erp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 库存控制器
 */
@RestController
@RequestMapping("/api/erp/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 获取库存列表
     */
    @GetMapping
    public ResponseEntity<Page<Inventory>> getInventoryList(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(inventoryService.getInventoryList(tenantId, page, size, category));
    }

    /**
     * 获取低库存商品
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStock(@RequestParam Long tenantId) {
        return ResponseEntity.ok(inventoryService.getLowStockItems(tenantId));
    }

    /**
     * 创建库存商品
     */
    @PostMapping
    public ResponseEntity<Inventory> createInventory(
            @RequestParam Long tenantId,
            @RequestBody Inventory inventory) {
        inventory.setTenantId(tenantId);
        return ResponseEntity.ok(inventoryService.createInventory(inventory));
    }

    /**
     * 更新库存数量
     */
    @PatchMapping("/{inventoryId}/quantity")
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @RequestParam Long tenantId,
            @PathVariable Long inventoryId,
            @RequestBody Map<String, Integer> request) {
        boolean success = inventoryService.updateQuantity(tenantId, inventoryId, request.get("quantity"));
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 获取库存统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@RequestParam Long tenantId) {
        return ResponseEntity.ok(inventoryService.getInventoryStats(tenantId));
    }
}
