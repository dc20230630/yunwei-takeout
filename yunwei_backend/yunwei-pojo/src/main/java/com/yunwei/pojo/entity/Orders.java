package com.yunwei.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体，对应 orders 表。
 */
@Data
public class Orders {

    private Long id;

    // 业务订单号，不是数据库主键
    private String number;

    // 1待付款、2待接单、3已接单、4派送中、5已完成、6已取消、7退款
    private Integer status;

    private Long userId;
    private Long addressBookId;

    private LocalDateTime orderTime;
    private LocalDateTime checkoutTime;

    // 1微信支付、2支付宝支付
    private Integer payMethod;

    // 0未支付、1已支付、2退款
    private Integer payStatus;

    private BigDecimal amount;
    private String remark;

    // 下单时保存地址快照，后续地址簿修改不影响历史订单
    private String phone;
    private String address;
    private String userName;
    private String consignee;

    private String cancelReason;
    private String rejectionReason;
    private LocalDateTime cancelTime;
    private LocalDateTime estimatedDeliveryTime;

    // 1立即送出、0选择具体时间
    private Integer deliveryStatus;
    private LocalDateTime deliveryTime;

    private Integer packAmount;

    private Integer tablewareNumber;

    // 1按餐量提供、0选择具体数量
    private Integer tablewareStatus;
}