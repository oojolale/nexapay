package com.nexapay.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 订单实体
 */
@Data
@TableName("orders")
public class Order {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private UUID uuid;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("order_number")
    private String orderNumber;

    @TableField("customer_name")
    private String customerName;

    @TableField("customer_email")
    private String customerEmail;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("currency")
    private String currency;

    @TableField("status")
    private String status;

    @TableField("payment_status")
    private String paymentStatus;

    @TableField("items")
    private String items;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
