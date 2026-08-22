package com.apimgt.apiordermgt.common.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceBatchIssueRequest {

    @NotEmpty(message = "请选择订单")
    private List<Long> orderIds;
    @NotNull(message = "开票日期不能为空")
    @PastOrPresent(message = "开票日期不能晚于今天")
    private LocalDate invoiceDate;
    @NotBlank(message = "发票编号不能为空")
    @Size(min = 1, max = 100, message = "发票编号长度不正确")
    private String invoiceNo;
    @NotNull(message = "请选择发票抬头")
    private Long invoiceTitleId;
}
