package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrdersSubmitDTO {

    @NotNull(message = "请选择收货地址")
    private Long addressBookId;

    @Size(max = 100, message = "备注不能超过100个字符")
    private String remark;

    // 1 微信支付、2 支付宝支付
    @NotNull(message = "请选择支付方式")
    private Integer payMethod;

    // 1 立即送出、0 预约配送
    @NotNull(message = "请选择配送方式")
    private Integer deliveryStatus;

    // 预约配送时由 Service 校验不能为空
    private LocalDateTime deliveryTime;

    // 1 按餐量提供、0 自选餐具数量
    @NotNull(message = "请选择餐具方式")
    private Integer tablewareStatus;

    // 自选餐具时由 Service 校验必须大于 0
    @Min(value = 1, message = "餐具数量必须大于0")
    private Integer tablewareNumber;
}
