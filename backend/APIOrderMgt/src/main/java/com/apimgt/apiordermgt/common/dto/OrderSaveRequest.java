package com.apimgt.apiordermgt.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSaveRequest {

    @NotBlank(message = "订单编号不能为空")
    @Size(max = 100, message = "订单编号不能超过 100 个字符")
    private String orderNo;

    @NotNull(message = "请选择提供商")
    private Long providerId;

    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额必须大于 0")
    private BigDecimal amount;

    @NotBlank(message = "请选择支付方式")
    @Pattern(
            regexp = "ALIPAY|WECHAT|BANK_CARD",
            message = "支付方式不正确"
    )
    private String paymentMethod;

}
