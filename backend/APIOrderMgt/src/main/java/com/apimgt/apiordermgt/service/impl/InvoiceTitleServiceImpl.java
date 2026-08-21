package com.apimgt.apiordermgt.service.impl;

import com.apimgt.apiordermgt.common.constant.InvoiceTitleConstant;
import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.entity.InvoiceTitleEntity;
import com.apimgt.apiordermgt.common.exception.BusinessException;
import com.apimgt.apiordermgt.common.dto.InvoiceTitleSaveRequest;
import com.apimgt.apiordermgt.mapper.InvoiceTitleMapper;
import com.apimgt.apiordermgt.common.vo.InvoiceTitleOptionVO;
import com.apimgt.apiordermgt.common.vo.InvoiceTitleVO;
import com.apimgt.apiordermgt.service.InvoiceTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceTitleServiceImpl implements InvoiceTitleService {

    private final InvoiceTitleMapper invoiceTitleMapper;

    public List<InvoiceTitleVO> list(String titleType) {
        validateTitleTypeFilter(titleType);
        return invoiceTitleMapper.selectVoList(titleType);
    }

    public List<InvoiceTitleOptionVO> options() {
        return List.of(
                new InvoiceTitleOptionVO(
                        "个人",
                        toOptionItems(list(InvoiceTitleConstant.TITLE_TYPE_PERSONAL))
                ),
                new InvoiceTitleOptionVO(
                        "企业",
                        toOptionItems(list(InvoiceTitleConstant.TITLE_TYPE_COMPANY))
                )
        );
    }

    public InvoiceTitleVO detail(Long id) {
        InvoiceTitleEntity entity = invoiceTitleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "发票抬头不存在");
        }
        return toView(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public InvoiceTitleVO create(
            InvoiceTitleSaveRequest request
    ) {
        normalizeAndValidate(request);
        validateUnique(request, null);
        InvoiceTitleEntity entity = toEntity(request);
        invoiceTitleMapper.insert(entity);
        return detail(entity.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public InvoiceTitleVO update(Long id, InvoiceTitleSaveRequest request) {
        InvoiceTitleEntity entity = requireEntity(id);
        normalizeAndValidate(request);
        validateUnique(request, id);
        entity.setTitleType(request.getTitleType());
        entity.setName(request.getName());
        entity.setTaxCode(request.getTaxCode());
        entity.setUpdatedAt(LocalDateTime.now().toString());
        entity.setVersion(entity.getVersion() + 1);
        if (invoiceTitleMapper.updateById(entity) != 1) {
            throw new BusinessException("发票抬头修改失败");
        }
        return detail(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(BatchIdRequest request) {
        int affectedRows = invoiceTitleMapper.deleteByIdList(request.getIds());
        if (affectedRows != request.getIds().size()) {
            throw new BusinessException("部分发票抬头不存在，未执行删除");
        }
    }

    // ========================================================================
    // ========================================================================

    private InvoiceTitleEntity requireEntity(Long id) {
        InvoiceTitleEntity entity = invoiceTitleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "发票抬头不存在");
        }
        return entity;
    }

    private List<InvoiceTitleOptionVO.OptionItem> toOptionItems(
            List<InvoiceTitleVO> invoiceTitles) {
        return invoiceTitles.stream()
                .map(title -> new InvoiceTitleOptionVO.OptionItem(
                        title.getId(),
                        title.getName()
                ))
                .collect(Collectors.toList());
    }

    private void normalizeAndValidate(InvoiceTitleSaveRequest request) {
        request.setTitleType(request.getTitleType().trim());
        request.setName(request.getName().trim());
        String taxCode = request.getTaxCode() == null
                ? null
                : request.getTaxCode().trim().toUpperCase();
        if (InvoiceTitleConstant.TITLE_TYPE_PERSONAL.equals(
                request.getTitleType())) {
            request.setTaxCode(null);
            return;
        }
        if (!InvoiceTitleConstant.TITLE_TYPE_COMPANY.equals(
                request.getTitleType())) {
            throw new BusinessException("抬头类型只能为个人或企业");
        }
        if (taxCode == null || taxCode.isBlank()) {
            throw new BusinessException("企业抬头必须填写税号");
        }
        request.setTaxCode(taxCode);
    }

    private void validateUnique(InvoiceTitleSaveRequest request, Long excludeId) {
        if (invoiceTitleMapper.countByNameAndType(
                request.getName(), request.getTitleType(), excludeId
        ) > 0) {
            throw new BusinessException(409, "同类型抬头名称已存在");
        }
        if (request.getTaxCode() != null
                && invoiceTitleMapper.countByTaxCode(request.getTaxCode(), excludeId) > 0
        ) {
            throw new BusinessException(409, "企业税号已存在");
        }
    }

    private InvoiceTitleEntity toEntity(InvoiceTitleSaveRequest request) {
        InvoiceTitleEntity entity = new InvoiceTitleEntity();
        entity.setTitleType(request.getTitleType());
        entity.setName(request.getName());
        entity.setTaxCode(request.getTaxCode());
        entity.setCreatedAt(LocalDateTime.now().toString());
        entity.setUpdatedAt(entity.getCreatedAt());
        entity.setVersion(0L);
        return entity;
    }

    private InvoiceTitleVO toView(InvoiceTitleEntity entity) {
        InvoiceTitleVO view = new InvoiceTitleVO();
        view.setId(entity.getId());
        view.setTitleType(entity.getTitleType());
        view.setName(entity.getName());
        view.setTaxCode(entity.getTaxCode());
        view.setCreatedAt(entity.getCreatedAt());
        view.setUpdatedAt(entity.getUpdatedAt());
        view.setVersion(entity.getVersion());
        return view;
    }

    private void validateTitleTypeFilter(String titleType) {
        if (titleType == null || titleType.isBlank()) {
            return;
        }
        boolean valid = InvoiceTitleConstant.TITLE_TYPE_PERSONAL.equals(titleType)
                || InvoiceTitleConstant.TITLE_TYPE_COMPANY.equals(titleType);
        if (!valid) {
            throw new BusinessException("抬头类型筛选条件不正确");
        }
    }

}
