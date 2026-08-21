package com.apimgt.apiordermgt.controller;

import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.dto.Result;
import com.apimgt.apiordermgt.common.dto.InvoiceTitleSaveRequest;
import com.apimgt.apiordermgt.service.InvoiceTitleService;
import com.apimgt.apiordermgt.common.vo.InvoiceTitleOptionVO;
import com.apimgt.apiordermgt.common.vo.InvoiceTitleVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice-titles")
@RequiredArgsConstructor
public class InvoiceTitleController {

    private final InvoiceTitleService invoiceTitleService;

    @GetMapping
    public Result<List<InvoiceTitleVO>> list(
            @RequestParam(required = false) String titleType
    ) {
        return Result.success(invoiceTitleService.list(titleType));
    }

    @GetMapping("/options")
    public Result<List<InvoiceTitleOptionVO>> options() {
        return Result.success(invoiceTitleService.options());
    }

    @GetMapping("/{id}")
    public Result<InvoiceTitleVO> detail(@PathVariable Long id
    ) {
        return Result.success(invoiceTitleService.detail(id));
    }

    @PostMapping
    public Result<InvoiceTitleVO> create(
            @Valid @RequestBody InvoiceTitleSaveRequest request
    ) {
        return Result.success("发票抬头创建成功", invoiceTitleService.create(request));
    }

    @PutMapping("/{id}")
    public Result<InvoiceTitleVO> update(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceTitleSaveRequest request
    ) {
        return Result.success("发票抬头修改成功", invoiceTitleService.update(id, request));
    }

    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(
            @Valid @RequestBody BatchIdRequest request
    ) {
        invoiceTitleService.batchDelete(request);
        return Result.success("发票抬头删除成功", null);
    }

}
