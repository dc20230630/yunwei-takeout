package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrdersPaymentDTO {

    @NotBlank(message = "订单号不能为空")
    private String orderNumber;
}
