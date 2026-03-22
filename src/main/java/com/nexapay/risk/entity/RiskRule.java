package com.nexapay.risk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 风控规则实体
 */
@Data
@TableName("risk_rule")
public class RiskRule {

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

    @TableField("rule_type")
    private String ruleType;

    @TableField("conditions")
    private String conditions;

    @TableField("action")
    private String action;

    @TableField("priority")
    private Integer priority;

    @TableField("status")
    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
