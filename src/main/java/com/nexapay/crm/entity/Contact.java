package com.nexapay.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 联系人实体
 */
@Data
@TableName("contact")
public class Contact {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private UUID uuid;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("name")
    private String name;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("company")
    private String company;

    @TableField("position")
    private String position;

    @TableField("pipeline_stage")
    private String pipelineStage;

    @TableField("lead_value")
    private BigDecimal leadValue;

    @TableField("tags")
    private String tags;

    @TableField("notes")
    private String notes;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
