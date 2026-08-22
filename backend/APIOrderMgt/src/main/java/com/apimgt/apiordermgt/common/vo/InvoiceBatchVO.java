package com.apimgt.apiordermgt.common.vo;

import lombok.Data;
import java.util.List;

@Data
public class InvoiceBatchVO {
    private Long id;
    private String invoiceDate;
    private String invoiceNo;
    private Long invoiceTitleId;
    private String invoiceTitleName;
    private String invoiceTitleType;
    private String invoiceTaxCode;
    private Long totalAmountCent;
    private String status;
    private Long replacedFromId;
    private List<Long> orderIds;
    private String createdAt;
    private String updatedAt;
    private Long version;
}
