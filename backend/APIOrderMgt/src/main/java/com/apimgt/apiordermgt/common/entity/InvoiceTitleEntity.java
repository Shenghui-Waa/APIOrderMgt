package com.apimgt.apiordermgt.common.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("invoice_title")
public class InvoiceTitleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title_type")
    private String titleType;

    private String name;

    @TableField(value = "tax_code", updateStrategy = FieldStrategy.ALWAYS)
    private String taxCode;

    @TableField("created_at")
    private String createdAt;

    @TableField("updated_at")
    private String updatedAt;

    private Long version;

}
