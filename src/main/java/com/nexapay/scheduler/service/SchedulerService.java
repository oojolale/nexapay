package com.nexapay.scheduler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.scheduler.entity.ScheduledTask;
import com.nexapay.scheduler.mapper.ScheduledTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 调度任务服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final ScheduledTaskMapper taskMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "nexapay:lock:";

    /**
     * 获取任务列表
     */
    public Page<ScheduledTask> getTasks(Long tenantId, int page, int size) {
        Page<ScheduledTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ScheduledTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduledTask::getTenantId, tenantId);
        wrapper.orderByDesc(ScheduledTask::getCreatedAt);
        return taskMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 创建任务
     */
    public ScheduledTask createTask(ScheduledTask task) {
        task.setUuid(UUID.randomUUID());
        task.setRunCount(0L);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        // Calculate next run time
        task.setNextRunAt(calculateNextRunTime(task.getCronExpression()));
        
        taskMapper.insert(task);
        return task;
    }

    /**
     * 执行任务（带分布式锁）
     */
    public boolean executeTask(Long taskId) {
        ScheduledTask task = taskMapper.selectById(taskId);
        if (task == null) return false;

        String lockKey = LOCK_PREFIX + task.getUuid();
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.MINUTES);
        
        if (!Boolean.TRUE.equals(acquired)) {
            log.warn("Task {} is already running", task.getName());
            return false;
        }

        try {
            log.info("Executing task: {}", task.getName());
            
            // Execute task based on type
            switch (task.getTaskType()) {
                case "REPORT":
                    executeReportTask(task);
                    break;
                case "SYNC":
                    executeSyncTask(task);
                    break;
                case "RISK_CALC":
                    executeRiskCalcTask(task);
                    break;
                default:
                    log.warn("Unknown task type: {}", task.getTaskType());
            }
            
            // Update task status
            task.setLastRunAt(LocalDateTime.now());
            task.setRunCount(task.getRunCount() + 1);
            task.setNextRunAt(calculateNextRunTime(task.getCronExpression()));
            taskMapper.updateById(task);
            
            return true;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private void executeReportTask(ScheduledTask task) {
        log.info("Generating report for tenant: {}", task.getTenantId());
    }

    private void executeSyncTask(ScheduledTask task) {
        log.info("Syncing data for tenant: {}", task.getTenantId());
    }

    private void executeRiskCalcTask(ScheduledTask task) {
        log.info("Calculating risk scores for tenant: {}", task.getTenantId());
    }

    /**
     * 计算下次运行时间
     */
    private LocalDateTime calculateNextRunTime(String cronExpression) {
        // Simplified: return next hour
        return LocalDateTime.now().plusHours(1);
    }

    /**
     * 获取待执行的任务
     */
    public List<ScheduledTask> getPendingTasks() {
        LambdaQueryWrapper<ScheduledTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduledTask::getStatus, "ACTIVE")
               .le(ScheduledTask::getNextRunAt, LocalDateTime.now());
        return taskMapper.selectList(wrapper);
    }

    /**
     * 更新任务状态
     */
    public boolean updateTaskStatus(Long taskId, String status) {
        ScheduledTask task = taskMapper.selectById(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setUpdatedAt(LocalDateTime.now());
            return taskMapper.updateById(task) > 0;
        }
        return false;
    }
}
