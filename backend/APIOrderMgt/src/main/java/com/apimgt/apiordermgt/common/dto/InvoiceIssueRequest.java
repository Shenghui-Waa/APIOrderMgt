package com.apimgt.apiordermgt.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceIssueRequest {

    @NotNull(message = "开票日期不能为空")
    @PastOrPresent(message = "开票日期不能晚于今天")
    private LocalDate invoiceDate;

    @NotBlank(message = "发票编号不能为空")
    @Size(max = 100, message = "发票编号不能超过 100 个字符")
    private String invoiceNo;

    @NotNull(message = "请选择发票抬头")
    private Long invoiceTitleId;

}
