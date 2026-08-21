package com.apimgt.apiordermgt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private Integer page;
    private Integer pageSize;
    private Long total;
    private List<T> records;

}
