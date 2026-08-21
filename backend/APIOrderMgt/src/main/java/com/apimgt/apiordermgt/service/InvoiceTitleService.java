package com.apimgt.apiordermgt.service;

import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.dto.InvoiceTitleSaveRequest;
import com.apimgt.apiordermgt.common.vo.InvoiceTitleOptionVO;
import com.apimgt.apiordermgt.common.vo.InvoiceTitleVO;

import java.util.List;

public interface InvoiceTitleService {

    public List<InvoiceTitleVO> list(String titleType);

    public List<InvoiceTitleOptionVO> options();

    public InvoiceTitleVO detail(Long id);

    public InvoiceTitleVO create(InvoiceTitleSaveRequest request);

    public InvoiceTitleVO update(Long id, InvoiceTitleSaveRequest request);

    public void batchDelete(BatchIdRequest request);

}
