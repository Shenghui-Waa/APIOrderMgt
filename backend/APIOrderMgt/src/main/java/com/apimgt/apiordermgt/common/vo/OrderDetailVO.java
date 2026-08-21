package com.apimgt.apiordermgt.common.vo;

import lombok.Data;

@Data
public class OrderDetailVO {

    private Long id;
    private String orderNo;
    private Long providerId;
    private String providerName;
    private String providerWebsiteUrl;
    private Long amountCent;
    private String paymentMethod;
    private String invoiceStatus;
    private String invoiceDate;
    private String invoiceNo;
    private Long invoiceTitleId;
    private String invoiceTitleName;
    private String invoiceTitleType;
    private String invoiceTaxCode;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;
    private Long version;

}
