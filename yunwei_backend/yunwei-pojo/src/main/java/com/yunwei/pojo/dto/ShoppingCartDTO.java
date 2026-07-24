package com.yunwei.pojo.dto;

import lombok.Data;

/**
 * 加入或减少购物车时，前端传入的商品标识。
 */
@Data
public class ShoppingCartDTO {

    private Long dishId;
    private Long setmealId;
    private String dishFlavor;
}