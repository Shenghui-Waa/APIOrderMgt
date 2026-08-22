package com.apimgt.apiordermgt.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("invoice_batch_order")
public class InvoiceBatchOrderEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("batch_id")
    private Long batchId;
    @TableField("order_id")
    private Long orderId;
    @TableField("amount_cent")
    private Long amountCent;
}
