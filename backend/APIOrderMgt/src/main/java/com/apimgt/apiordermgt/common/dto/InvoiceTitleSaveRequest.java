package com.apimgt.apiordermgt.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InvoiceTitleSaveRequest {

    @NotBlank(message = "抬头类型不能为空")
    private String titleType;

    @NotBlank(message = "抬头名称不能为空")
    @Size(max = 150, message = "抬头名称不能超过 150 个字符")
    private String name;

    @Pattern(
            regexp = "^$|^[A-Za-z0-9]{15,20}$",
            message = "税号格式不正确"
    )
    private String taxCode;

}
