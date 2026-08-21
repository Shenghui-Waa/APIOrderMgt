package com.apimgt.apiordermgt.service;

import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.dto.InvoiceIssueRequest;
import com.apimgt.apiordermgt.common.dto.OrderSaveRequest;
import com.apimgt.apiordermgt.common.dto.PageResult;
import com.apimgt.apiordermgt.common.vo.OrderDetailVO;
import com.apimgt.apiordermgt.common.vo.OrderListVO;

import java.util.List;

public interface OrderService {

    public PageResult<OrderListVO> page(
            String keyword, List<Long> providerIds,
            String invoiceStatus, String invoiceTitleType,
            int page, int pageSize, boolean recycled);

    public OrderDetailVO detail(Long id);

    public OrderDetailVO create(OrderSaveRequest request);

    public OrderDetailVO update(Long id, OrderSaveRequest request);

    public OrderDetailVO issueInvoice(Long id, InvoiceIssueRequest request);

    public void batchDelete(BatchIdRequest request);

    public OrderDetailVO restore(Long id);

}
