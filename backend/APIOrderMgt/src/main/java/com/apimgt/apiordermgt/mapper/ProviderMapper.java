package com.apimgt.apiordermgt.mapper;

import com.apimgt.apiordermgt.common.entity.APIProviderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProviderMapper extends BaseMapper<APIProviderEntity> {

    @Select("SELECT id, name, website_url AS websiteUrl," +
            "created_at AS createdAt, updated_at AS updatedAt, version " +
            "FROM api_provider ORDER BY name ASC")
    List<APIProviderEntity> selectAllOrdered();


    Long countByName(
            @Param("name") String name,
            @Param("excludeId") Long excludeId
    );


    Integer deleteByIdList(@Param("ids") List<Long> ids);

}
