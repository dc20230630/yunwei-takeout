package com.yunwei.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细实体，对应 order_detail 表。
 */
@Data
public class OrderDetail {

    private Long id;

    // 商品快照字段，避免菜品或套餐后来修改影响历史订单
    private String name;
    private String image;

    private Long orderId;
    private Long dishId;
    private Long setmealId;

    // 用户下单时选择的菜品口味
    private String dishFlavor;

    private Integer number;
    private BigDecimal amount;
}