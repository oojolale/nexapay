package com.nexapay.scheduler.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.scheduler.entity.ScheduledTask;
import com.nexapay.scheduler.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 调度控制器
 */
@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerService schedulerService;

    /**
     * 获取任务列表
     */
    @GetMapping("/tasks")
    public ResponseEntity<Page<ScheduledTask>> getTasks(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(schedulerService.getTasks(tenantId, page, size));
    }

    /**
     * 创建任务
     */
    @PostMapping("/tasks")
    public ResponseEntity<ScheduledTask> createTask(
            @RequestParam Long tenantId,
            @RequestBody ScheduledTask task) {
        task.setTenantId(tenantId);
        return ResponseEntity.ok(schedulerService.createTask(task));
    }

    /**
     * 执行任务
     */
    @PostMapping("/tasks/{taskId}/execute")
    public ResponseEntity<Map<String, Object>> executeTask(@PathVariable Long taskId) {
        boolean success = schedulerService.executeTask(taskId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 更新任务状态
     */
    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> request) {
        boolean success = schedulerService.updateTaskStatus(taskId, request.get("status"));
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 获取待执行任务
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ScheduledTask>> getPendingTasks() {
        return ResponseEntity.ok(schedulerService.getPendingTasks());
    }
}
