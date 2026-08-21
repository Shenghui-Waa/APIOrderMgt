package com.apimgt.apiordermgt.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("api_order")
public class APIOrderEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("provider_id")
    private Long providerId;

    @TableField("provider_name_snapshot")
    private String providerNameSnapshot;

    @TableField("provider_website_url_snapshot")
    private String providerWebsiteUrlSnapshot;

    @TableField("amount_cent")
    private Long amountCent;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("invoice_status")
    private String invoiceStatus;

    @TableField("invoice_date")
    private String invoiceDate;

    @TableField("invoice_no")
    private String invoiceNo;

    @TableField("invoice_title_id")
    private Long invoiceTitleId;

    @TableField("invoice_title_name_snapshot")
    private String invoiceTitleNameSnapshot;

    @TableField("invoice_title_type_snapshot")
    private String invoiceTitleTypeSnapshot;

    @TableField("invoice_tax_code_snapshot")
    private String invoiceTaxCodeSnapshot;

    @TableField("deleted_at")
    private String deletedAt;

    @TableField("created_at")
    private String createdAt;

    @TableField("updated_at")
    private String updatedAt;

    private Long version;

}
