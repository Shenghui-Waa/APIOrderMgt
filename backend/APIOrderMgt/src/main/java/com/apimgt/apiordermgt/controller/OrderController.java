package com.apimgt.apiordermgt.controller;

import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.dto.PageResult;
import com.apimgt.apiordermgt.common.dto.Result;
import com.apimgt.apiordermgt.common.dto.InvoiceIssueRequest;
import com.apimgt.apiordermgt.common.dto.OrderSaveRequest;
import com.apimgt.apiordermgt.service.OrderService;
import com.apimgt.apiordermgt.common.vo.OrderDetailVO;
import com.apimgt.apiordermgt.common.vo.OrderListVO;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<PageResult<OrderListVO>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> providerIds,
            @RequestParam(required = false) String invoiceStatus,
            @RequestParam(required = false) String invoiceTitleType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(orderService.page(
                keyword, providerIds, invoiceStatus, invoiceTitleType,
                page, pageSize, false
        ));
    }

    @PostMapping
    public Result<OrderDetailVO> create(
            @Valid @RequestBody OrderSaveRequest request) {
        return Result.success("订单创建成功", orderService.create(request));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.success(orderService.detail(id));
    }

    @PutMapping("/{id}")
    public Result<OrderDetailVO> update(
            @PathVariable Long id,
            @Valid @RequestBody OrderSaveRequest request) {
        return Result.success("订单修改成功", orderService.update(id, request));
    }

    @PostMapping("/{id}/invoice")
    public Result<OrderDetailVO> issueInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceIssueRequest request) {
        return Result.success("发票开具成功", orderService.issueInvoice(id, request));
    }

    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@Valid @RequestBody BatchIdRequest request) {
        orderService.batchDelete(request);
        return Result.success("订单已移入回收站", null);
    }

    @GetMapping("/recycle-bin")
    public Result<PageResult<OrderListVO>> recycleBin(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.page(
                keyword,
                null, null, null,
                page, pageSize, true
        ));
    }

    @PostMapping("/{id}/restore")
    public Result<OrderDetailVO> restore(@PathVariable Long id) {
        return Result.success("订单恢复成功", orderService.restore(id));
    }

}
