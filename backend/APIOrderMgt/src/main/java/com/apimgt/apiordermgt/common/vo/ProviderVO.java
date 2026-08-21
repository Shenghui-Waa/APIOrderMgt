package com.apimgt.apiordermgt.common.vo;

import lombok.Data;

@Data
public class ProviderVO {

    private Long id;
    private String name;
    private String websiteUrl;
    private String createdAt;
    private String updatedAt;
    private Long version;

}
