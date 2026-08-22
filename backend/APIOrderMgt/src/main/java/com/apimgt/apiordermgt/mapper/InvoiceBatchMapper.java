package com.apimgt.apiordermgt.mapper;

import com.apimgt.apiordermgt.common.entity.InvoiceBatchEntity;
import com.apimgt.apiordermgt.common.entity.InvoiceBatchOrderEntity;
import com.apimgt.apiordermgt.common.vo.InvoiceBatchVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InvoiceBatchMapper extends BaseMapper<InvoiceBatchEntity> {

    @Select("SELECT COUNT(1) FROM invoice_batch WHERE invoice_no = #{invoiceNo}")
    Long countByInvoiceNo(@Param("invoiceNo") String invoiceNo);

    int insertBatchOrders(@Param("items") List<InvoiceBatchOrderEntity> items);

    InvoiceBatchVO selectDetail(@Param("id") Long id);

    List<Long> selectOrderIds(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("updatedAt") String updatedAt);

    @Select("SELECT status FROM invoice_batch WHERE id = #{id}")
    String selectStatus(@Param("id") Long id);
}
