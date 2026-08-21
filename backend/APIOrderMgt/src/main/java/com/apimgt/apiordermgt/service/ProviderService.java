package com.apimgt.apiordermgt.service;

import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.dto.ProviderSaveRequest;
import com.apimgt.apiordermgt.common.vo.ProviderVO;

import java.util.List;

public interface ProviderService {

    public List<ProviderVO> list();

    public ProviderVO detail(Long id);

    public ProviderVO create(ProviderSaveRequest request);

    public ProviderVO update(Long id, ProviderSaveRequest request);

    public void batchDelete(BatchIdRequest request);



}
