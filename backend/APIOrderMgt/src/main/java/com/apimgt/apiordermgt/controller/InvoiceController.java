package com.apimgt.apiordermgt.controller;

import com.apimgt.apiordermgt.common.dto.InvoiceBatchIssueRequest;
import com.apimgt.apiordermgt.common.dto.InvoiceReissueRequest;
import com.apimgt.apiordermgt.common.dto.Result;
import com.apimgt.apiordermgt.common.vo.InvoiceBatchVO;
import com.apimgt.apiordermgt.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public Result<InvoiceBatchVO> issue(@Valid @RequestBody InvoiceBatchIssueRequest request) {
        return Result.success("发票开具成功", invoiceService.issue(request));
    }

    @GetMapping("/{id}")
    public Result<InvoiceBatchVO> detail(@PathVariable Long id) {
        return Result.success(invoiceService.detail(id));
    }

    @PostMapping("/{id}/reissue")
    public Result<InvoiceBatchVO> reissue(@PathVariable Long id,
                                         @Valid @RequestBody InvoiceReissueRequest request) {
        return Result.success("发票重开成功", invoiceService.reissue(id, request));
    }

    @PostMapping("/{id}/void")
    public Result<InvoiceBatchVO> voidInvoice(@PathVariable Long id) {
        return Result.success("发票已作废", invoiceService.voidInvoice(id));
    }
}
