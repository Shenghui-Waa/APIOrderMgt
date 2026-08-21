package com.apimgt.apiordermgt.service.impl;

import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.entity.APIProviderEntity;
import com.apimgt.apiordermgt.common.exception.BusinessException;
import com.apimgt.apiordermgt.common.dto.ProviderSaveRequest;
import com.apimgt.apiordermgt.mapper.ProviderMapper;
import com.apimgt.apiordermgt.common.vo.ProviderVO;
import com.apimgt.apiordermgt.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderMapper providerMapper;

    public List<ProviderVO> list() {
        return providerMapper.selectAllOrdered().stream()
                .map(this::toView)
                .toList();
    }

    public ProviderVO detail(Long id) {
        return toView(requireEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public ProviderVO create(ProviderSaveRequest request) {
        normalize(request);
        validateName(request.getName(), null);
        APIProviderEntity entity = new APIProviderEntity();
        entity.setName(request.getName());
        entity.setWebsiteUrl(request.getWebsiteUrl());
        entity.setCreatedAt(LocalDateTime.now().toString());
        entity.setUpdatedAt(entity.getCreatedAt());
        entity.setVersion(0L);
        providerMapper.insert(entity);
        return detail(entity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public ProviderVO update(Long id, ProviderSaveRequest request) {
        APIProviderEntity entity = requireEntity(id);
        normalize(request);
        validateName(request.getName(), id);
        entity.setName(request.getName());
        entity.setWebsiteUrl(request.getWebsiteUrl());
        entity.setUpdatedAt(LocalDateTime.now().toString());
        entity.setVersion(entity.getVersion() + 1);
        if (providerMapper.updateById(entity) != 1) {
            throw new BusinessException("提供商修改失败");
        }
        return detail(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(BatchIdRequest request) {
        int affectedRows = providerMapper.deleteByIdList(request.getIds());
        if (affectedRows != request.getIds().size()) {
            throw new BusinessException("部分提供商不存在，未执行删除");
        }
    }

    private APIProviderEntity requireEntity(Long id) {
        APIProviderEntity entity = providerMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "提供商不存在");
        }
        return entity;
    }

    private void validateName(String name, Long excludeId) {
        if (providerMapper.countByName(name, excludeId) > 0) {
            throw new BusinessException(409, "提供商名称已存在");
        }
    }

    private void normalize(ProviderSaveRequest request) {
        request.setName(request.getName().trim());
        request.setWebsiteUrl(request.getWebsiteUrl().trim());
    }

    private ProviderVO toView(APIProviderEntity entity) {
        ProviderVO view = new ProviderVO();
        view.setId(entity.getId());
        view.setName(entity.getName());
        view.setWebsiteUrl(entity.getWebsiteUrl());
        view.setCreatedAt(entity.getCreatedAt());
        view.setUpdatedAt(entity.getUpdatedAt());
        view.setVersion(entity.getVersion());
        return view;
    }

}
