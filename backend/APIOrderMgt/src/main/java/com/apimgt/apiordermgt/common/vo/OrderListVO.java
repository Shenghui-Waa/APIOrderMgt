package com.apimgt.apiordermgt.common.vo;

import lombok.Data;

@Data
public class OrderListVO {

    private Long id;
    private String orderNo;
    private String providerName;
    private Long amountCent;
    private String paymentMethod;
    private String invoiceStatus;
    private String invoiceTitleType;
    private String invoiceTitleName;
    private String deletedAt;
    private String createdAt;
    private Long version;

}
