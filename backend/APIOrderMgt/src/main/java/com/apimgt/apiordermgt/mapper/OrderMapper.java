package com.apimgt.apiordermgt.mapper;

import com.apimgt.apiordermgt.common.entity.APIOrderEntity;
import com.apimgt.apiordermgt.common.vo.OrderDetailVO;
import com.apimgt.apiordermgt.common.vo.OrderListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<APIOrderEntity> {


    List<OrderListVO> selectPageRecords(
            @Param("keyword") String keyword,
            @Param("providerIds") List<Long> providerIds,
            @Param("invoiceStatus") String invoiceStatus,
            @Param("invoiceTitleType") String invoiceTitleType,
            @Param("recycled") boolean recycled,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset
    );


    Long countPageRecords(
            @Param("keyword") String keyword,
            @Param("providerIds") List<Long> providerIds,
            @Param("invoiceStatus") String invoiceStatus,
            @Param("invoiceTitleType") String invoiceTitleType,
            @Param("recycled") boolean recycled
    );


    OrderDetailVO selectDetailById(@Param("id") Long id);


    Long countByOrderNo(
            @Param("orderNo") String orderNo,
            @Param("excludeId") Long excludeId
    );

    @Select("SELECT COUNT(1) FROM api_order WHERE invoice_no = #{invoiceNo}")
    Long countByInvoiceNo(@Param("invoiceNo") String invoiceNo);


    Integer updateOrder(APIOrderEntity entity);


    Integer issueInvoice(APIOrderEntity entity);

    Integer issueInvoiceBatch(
            @Param("orderIds") List<Long> orderIds,
            @Param("invoiceDate") String invoiceDate,
            @Param("invoiceNo") String invoiceNo,
            @Param("invoiceTitleId") Long invoiceTitleId,
            @Param("invoiceTitleName") String invoiceTitleName,
            @Param("invoiceTitleType") String invoiceTitleType,
            @Param("invoiceTaxCode") String invoiceTaxCode,
            @Param("storeInvoiceNo") boolean storeInvoiceNo,
            @Param("updatedAt") String updatedAt
    );

    Integer clearInvoiceBatch(@Param("orderIds") List<Long> orderIds,
                              @Param("updatedAt") String updatedAt);


    Integer logicalDeleteByIds(
            @Param("ids") List<Long> ids,
            @Param("deletedAt") String deletedAt,
            @Param("updatedAt") String updatedAt
    );

    @Update("UPDATE api_order " +
            "SET deleted_at = NULL, updated_at = #{updatedAt}, version = version + 1 " +
            "WHERE id = #{id} AND deleted_at IS NOT NULL")
    Integer restore(@Param("id") Long id, @Param("updatedAt") String updatedAt);

}
