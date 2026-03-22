package com.nexapay.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("\"user\"")  // PostgreSQL中user是保留字，需要加双引号
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private UUID uuid;

    @TableField("tenantid")
    private Long tenantId;

    @TableField("username")
    private String username;

    @TableField("email")
    private String email;

    @TableField("passwordhash")
    private String passwordHash;

    @TableField("role")
    private String role;

    @TableField("status")
    private String status;

    @TableField("totpsecret")
    private String totpSecret;

    @TableField("lastloginat")
    private LocalDateTime lastLoginAt;

    @TableField(value = "createdat", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updatedat", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
