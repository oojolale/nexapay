package com.nexapay.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 交易实体
 */
@Data
@TableName("transaction")
public class Transaction {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private UUID uuid;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("transaction_id")
    private String transactionId;

    @TableField("order_id")
    private Long orderId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("currency")
    private String currency;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("status")
    private String status;

    @TableField("risk_score")
    private Integer riskScore;

    @TableField("risk_status")
    private String riskStatus;

    @TableField("metadata")
    private String metadata;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
