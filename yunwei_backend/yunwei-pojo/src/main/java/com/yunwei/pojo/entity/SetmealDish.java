package com.yunwei.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 套餐与菜品的关联实体，对应 setmeal_dish 表。
 */
@Data
public class SetmealDish {

    private Long id;
    private Long setmealId;
    private Long dishId;
    private String name;
    private BigDecimal price;
    private Integer copies;
}
