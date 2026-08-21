package com.apimgt.apiordermgt.mapper;

import com.apimgt.apiordermgt.common.entity.InvoiceTitleEntity;
import com.apimgt.apiordermgt.common.vo.InvoiceTitleVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InvoiceTitleMapper extends BaseMapper<InvoiceTitleEntity> {

    List<InvoiceTitleVO> selectVoList(
            @Param("titleType") String titleType
    );


    Long countByNameAndType(
            @Param("name") String name,
            @Param("titleType") String titleType,
            @Param("excludeId") Long excludeId
    );


    Long countByTaxCode(
            @Param("taxCode") String taxCode,
            @Param("excludeId") Long excludeId
    );


    Integer deleteByIdList(@Param("ids") List<Long> ids);

}
