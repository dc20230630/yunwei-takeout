package com.yunwei.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体，对应 shopping_cart 表。
 */
@Data
public class ShoppingCart {

    private Long id;
    private String name;
    private String image;

    private Long userId;
    private Long dishId;
    private Long setmealId;

    // 用户本次选择的具体口味
    private String dishFlavor;

    private Integer number;
    private BigDecimal amount;
    private LocalDateTime createTime;
}