package com.apimgt.apiordermgt.service.impl;

import com.apimgt.apiordermgt.common.constant.OrderConstant;
import com.apimgt.apiordermgt.common.dto.InvoiceBatchIssueRequest;
import com.apimgt.apiordermgt.common.dto.InvoiceReissueRequest;
import com.apimgt.apiordermgt.common.entity.APIOrderEntity;
import com.apimgt.apiordermgt.common.entity.InvoiceBatchEntity;
import com.apimgt.apiordermgt.common.entity.InvoiceBatchOrderEntity;
import com.apimgt.apiordermgt.common.entity.InvoiceTitleEntity;
import com.apimgt.apiordermgt.common.exception.BusinessException;
import com.apimgt.apiordermgt.common.vo.InvoiceBatchVO;
import com.apimgt.apiordermgt.mapper.InvoiceBatchMapper;
import com.apimgt.apiordermgt.mapper.InvoiceTitleMapper;
import com.apimgt.apiordermgt.mapper.OrderMapper;
import com.apimgt.apiordermgt.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceBatchMapper invoiceBatchMapper;
    private final InvoiceTitleMapper invoiceTitleMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoiceBatchVO issue(InvoiceBatchIssueRequest request) {
        if (request.getOrderIds().size() != new HashSet<>(request.getOrderIds()).size()) {
            throw new BusinessException("订单不能重复");
        }
        String invoiceNo = request.getInvoiceNo().trim();
        if (invoiceBatchMapper.countByInvoiceNo(invoiceNo) > 0
                || orderMapper.countByInvoiceNo(invoiceNo) > 0) {
            throw new BusinessException(409, "发票编号已存在");
        }
        InvoiceTitleEntity title = requireTitle(request.getInvoiceTitleId());
        List<APIOrderEntity> orders = orderMapper.selectBatchIds(request.getOrderIds());
        if (orders.size() != request.getOrderIds().size()) {
            throw new BusinessException(404, "存在不存在的订单");
        }
        long total = 0;
        for (APIOrderEntity order : orders) {
            if (order.getDeletedAt() != null
                    || !OrderConstant.INVOICE_STATUS_UNISSUED.equals(order.getInvoiceStatus())) {
                throw new BusinessException("仅可对未开票且未删除订单开票");
            }
            total = Math.addExact(total, order.getAmountCent());
        }
        String now = LocalDateTime.now().toString();
        InvoiceBatchEntity batch = buildBatch(request.getInvoiceDate().toString(), invoiceNo,
                title, total, null, now);
        invoiceBatchMapper.insert(batch);
        List<InvoiceBatchOrderEntity> links = orders.stream().map(order -> {
            InvoiceBatchOrderEntity link = new InvoiceBatchOrderEntity();
            link.setBatchId(batch.getId());
            link.setOrderId(order.getId());
            link.setAmountCent(order.getAmountCent());
            return link;
        }).toList();
        invoiceBatchMapper.insertBatchOrders(links);
        if (orderMapper.issueInvoiceBatch(request.getOrderIds(), batch.getInvoiceDate(),
                invoiceNo, title.getId(), title.getName(), title.getTitleType(),
                title.getTaxCode(), orders.size() == 1, now) != orders.size()) {
            throw new BusinessException("订单开票失败");
        }
        return detail(batch.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoiceBatchVO reissue(Long id, InvoiceReissueRequest request) {
        InvoiceBatchVO old = detail(id);
        if (!OrderConstant.INVOICE_BATCH_STATUS_ISSUED.equals(old.getStatus())) {
            throw new BusinessException("仅可重开有效发票");
        }
        voidInvoice(id);
        InvoiceBatchIssueRequest next = new InvoiceBatchIssueRequest();
        next.setOrderIds(old.getOrderIds());
        next.setInvoiceDate(request.getInvoiceDate());
        next.setInvoiceNo(request.getInvoiceNo());
        next.setInvoiceTitleId(request.getInvoiceTitleId());
        InvoiceBatchVO result = issue(next);
        InvoiceBatchEntity entity = invoiceBatchMapper.selectById(result.getId());
        entity.setReplacedFromId(id);
        invoiceBatchMapper.updateById(entity);
        return detail(result.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvoiceBatchVO voidInvoice(Long id) {
        InvoiceBatchVO batch = detail(id);
        if (!OrderConstant.INVOICE_BATCH_STATUS_ISSUED.equals(batch.getStatus())) {
            throw new BusinessException("发票已作废");
        }
        String now = LocalDateTime.now().toString();
        if (invoiceBatchMapper.updateStatus(id, OrderConstant.INVOICE_BATCH_STATUS_VOIDED, now) != 1) {
            throw new BusinessException("发票作废失败");
        }
        if (orderMapper.clearInvoiceBatch(batch.getOrderIds(), now)
                != batch.getOrderIds().size()) {
            throw new BusinessException("订单发票状态更新失败");
        }
        return detail(id);
    }

    @Override
    public InvoiceBatchVO detail(Long id) {
        InvoiceBatchVO batch = invoiceBatchMapper.selectDetail(id);
        if (batch == null) {
            throw new BusinessException(404, "发票批次不存在");
        }
        batch.setOrderIds(invoiceBatchMapper.selectOrderIds(id));
        return batch;
    }

    private InvoiceTitleEntity requireTitle(Long id) {
        InvoiceTitleEntity title = invoiceTitleMapper.selectById(id);
        if (title == null) {
            throw new BusinessException(404, "发票抬头不存在");
        }
        return title;
    }

    private InvoiceBatchEntity buildBatch(String date, String no, InvoiceTitleEntity title,
                                          long total, Long replacedFrom, String now) {
        InvoiceBatchEntity batch = new InvoiceBatchEntity();
        batch.setInvoiceDate(date);
        batch.setInvoiceNo(no);
        batch.setInvoiceTitleId(title.getId());
        batch.setInvoiceTitleNameSnapshot(title.getName());
        batch.setInvoiceTitleTypeSnapshot(title.getTitleType());
        batch.setInvoiceTaxCodeSnapshot(title.getTaxCode());
        batch.setTotalAmountCent(total);
        batch.setStatus(OrderConstant.INVOICE_BATCH_STATUS_ISSUED);
        batch.setReplacedFromId(replacedFrom);
        batch.setCreatedAt(now);
        batch.setUpdatedAt(now);
        batch.setVersion(0L);
        return batch;
    }
}
