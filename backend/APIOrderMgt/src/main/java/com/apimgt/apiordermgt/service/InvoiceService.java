package com.apimgt.apiordermgt.service;

import com.apimgt.apiordermgt.common.dto.InvoiceBatchIssueRequest;
import com.apimgt.apiordermgt.common.dto.InvoiceReissueRequest;
import com.apimgt.apiordermgt.common.vo.InvoiceBatchVO;

public interface InvoiceService {
    InvoiceBatchVO issue(InvoiceBatchIssueRequest request);
    InvoiceBatchVO reissue(Long id, InvoiceReissueRequest request);
    InvoiceBatchVO voidInvoice(Long id);
    InvoiceBatchVO detail(Long id);
}
