package com.apimgt.apiordermgt.common.vo;

import lombok.Data;

@Data
public class InvoiceTitleVO {

    private Long id;
    private String titleType;
    private String name;
    private String taxCode;
    private String createdAt;
    private String updatedAt;
    private Long version;

}
