package com.nexapay.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 调度任务实体
 */
@Data
@TableName("scheduled_task")
public class ScheduledTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private UUID uuid;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("task_type")
    private String taskType;

    @TableField("cron_expression")
    private String cronExpression;

    @TableField("payload")
    private String payload;

    @TableField("status")
    private String status;

    @TableField("last_run_at")
    private LocalDateTime lastRunAt;

    @TableField("next_run_at")
    private LocalDateTime nextRunAt;

    @TableField("run_count")
    private Long runCount;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
