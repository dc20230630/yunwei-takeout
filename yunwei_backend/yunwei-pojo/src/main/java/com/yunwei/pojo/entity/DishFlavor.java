package com.yunwei.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜品口味实体，对应 dish_flavor 表。
 */
@Data
public class DishFlavor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    // 所属菜品 ID，新增菜品后再赋值
    private Long dishId;

    // 口味名称，例如：辣度、甜味、忌口
    private String name;

    // JSON 字符串，例如：[\"不辣\",\"微辣\",\"中辣\"]
    private String value;
}
