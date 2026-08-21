package com.apimgt.apiordermgt.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InvoiceTitleOptionVO {

    private String label;
    private List<OptionItem> options;

    @Data
    @AllArgsConstructor
    public static class OptionItem {

        private Long value;
        private String label;

    }

}
