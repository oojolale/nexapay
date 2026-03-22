package com.nexapay.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API密钥实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("api_key")
public class ApiKey {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private UUID uuid;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("user_id")
    private Long userId;

    @TableField("name")
    private String name;

    @TableField("key_hash")
    private String keyHash;

    @TableField("permissions")
    private String permissions;

    @TableField("last_used_at")
    private LocalDateTime lastUsedAt;

    @TableField("expires_at")
    private LocalDateTime expiresAt;

    @TableField("status")
    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
