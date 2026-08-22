package com.apimgt.apiordermgt.service.impl;

import com.apimgt.apiordermgt.common.constant.OrderConstant;
import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.dto.PageResult;
import com.apimgt.apiordermgt.common.entity.APIOrderEntity;
import com.apimgt.apiordermgt.common.entity.APIProviderEntity;
import com.apimgt.apiordermgt.common.exception.BusinessException;
import com.apimgt.apiordermgt.common.dto.InvoiceIssueRequest;
import com.apimgt.apiordermgt.common.dto.InvoiceBatchIssueRequest;
import com.apimgt.apiordermgt.common.dto.OrderSaveRequest;
import com.apimgt.apiordermgt.mapper.OrderMapper;
import com.apimgt.apiordermgt.common.vo.OrderDetailVO;
import com.apimgt.apiordermgt.common.vo.OrderListVO;
import com.apimgt.apiordermgt.mapper.ProviderMapper;
import com.apimgt.apiordermgt.service.OrderService;
import com.apimgt.apiordermgt.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProviderMapper providerMapper;
    private final InvoiceService invoiceService;

    public PageResult<OrderListVO> page(
            String keyword,
            List<Long> providerIds,
            String invoiceStatus,
            String invoiceTitleType,
            int page, int pageSize, boolean recycled
    ) {
        validatePage(page, pageSize);
        validateInvoiceStatus(invoiceStatus);
        String normalizedKeyword = keyword == null ? null : keyword.trim();
        List<OrderListVO> records = orderMapper.selectPageRecords(
                normalizedKeyword,
                providerIds,
                invoiceStatus,
                invoiceTitleType,
                recycled,
                pageSize,
                (page - 1) * pageSize
        );
        long total = orderMapper.countPageRecords(
                normalizedKeyword,
                providerIds,
                invoiceStatus,
                invoiceTitleType,
                recycled
        );
        return new PageResult<>(page, pageSize, total, records);
    }

    public OrderDetailVO detail(Long id) {
        OrderDetailVO order = orderMapper.selectDetailById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO create(OrderSaveRequest request) {
        normalize(request);
        validateOrderNo(request.getOrderNo(), null);
        APIProviderEntity provider = requireProvider(request.getProviderId());
        APIOrderEntity entity = new APIOrderEntity();
        entity.setOrderNo(request.getOrderNo());
        entity.setProviderId(provider.getId());
        entity.setProviderNameSnapshot(provider.getName());
        entity.setProviderWebsiteUrlSnapshot(provider.getWebsiteUrl());
        entity.setAmountCent(amountToCent(request));
        entity.setPaymentMethod(request.getPaymentMethod());
        entity.setInvoiceStatus(OrderConstant.INVOICE_STATUS_UNISSUED);
        entity.setCreatedAt(LocalDateTime.now().toString());
        entity.setUpdatedAt(entity.getCreatedAt());
        entity.setVersion(0L);
        orderMapper.insert(entity);
        return detail(entity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO update(Long id, OrderSaveRequest request) {
        OrderDetailVO order = detail(id);
        if (order.getDeletedAt() != null) {
            throw new BusinessException("已删除订单不能修改");
        }
        if (OrderConstant.INVOICE_STATUS_ISSUED.equals(order.getInvoiceStatus())) {
            throw new BusinessException("已开票订单不能修改");
        }
        normalize(request);
        validateOrderNo(request.getOrderNo(), id);
        APIProviderEntity provider = requireProvider(request.getProviderId());
        APIOrderEntity entity = new APIOrderEntity();
        entity.setId(id);
        entity.setOrderNo(request.getOrderNo());
        entity.setProviderId(provider.getId());
        entity.setProviderNameSnapshot(provider.getName());
        entity.setProviderWebsiteUrlSnapshot(provider.getWebsiteUrl());
        entity.setAmountCent(amountToCent(request));
        entity.setPaymentMethod(request.getPaymentMethod());
        entity.setUpdatedAt(LocalDateTime.now().toString());
        if (orderMapper.updateOrder(entity) != 1) {
            throw new BusinessException("订单修改失败");
        }
        return detail(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO issueInvoice(Long id, InvoiceIssueRequest request) {
        InvoiceBatchIssueRequest batchRequest = new InvoiceBatchIssueRequest();
        batchRequest.setOrderIds(List.of(id));
        batchRequest.setInvoiceDate(request.getInvoiceDate());
        batchRequest.setInvoiceNo(request.getInvoiceNo());
        batchRequest.setInvoiceTitleId(request.getInvoiceTitleId());
        invoiceService.issue(batchRequest);
        return detail(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(BatchIdRequest request) {
        String now = LocalDateTime.now().toString();
        int affectedRows = orderMapper.logicalDeleteByIds(
                request.getIds(), now, now
        );
        if (affectedRows != request.getIds().size()) {
            throw new BusinessException("存在无效或已删除订单，未执行删除");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO restore(Long id) {
        if (orderMapper.restore(id, LocalDateTime.now().toString()) != 1) {
            throw new BusinessException("订单不存在或未被删除");
        }
        return detail(id);
    }

    private APIProviderEntity requireProvider(Long providerId) {
        APIProviderEntity provider = providerMapper.selectById(providerId);
        if (provider == null) {
            throw new BusinessException(404, "提供商不存在");
        }
        return provider;
    }

    private void normalize(OrderSaveRequest request) {
        request.setOrderNo(request.getOrderNo().trim());
    }

    private void validateOrderNo(String orderNo, Long excludeId) {
        if (orderMapper.countByOrderNo(orderNo, excludeId) > 0) {
            throw new BusinessException(409, "订单编号已存在");
        }
    }

    private long amountToCent(OrderSaveRequest request) {
        return request.getAmount().movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private void validateInvoiceStatus(String invoiceStatus) {
        if (invoiceStatus == null || invoiceStatus.isBlank()) {
            return;
        }
        boolean valid = OrderConstant.INVOICE_STATUS_UNISSUED.equals(invoiceStatus)
                || OrderConstant.INVOICE_STATUS_ISSUED.equals(invoiceStatus);
        if (!valid) {
            throw new BusinessException("开票状态筛选条件不正确");
        }
    }

    private void validatePage(int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new BusinessException("分页参数不正确");
        }
    }

}
