package com.apimgt.apiordermgt.common.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("api_provider")
public class APIProviderEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;

    @TableField("website_url")
    private String websiteUrl;

    @TableField("created_at")
    private String createdAt;

    @TableField("updated_at")
    private String updatedAt;

    private Long version;

}
