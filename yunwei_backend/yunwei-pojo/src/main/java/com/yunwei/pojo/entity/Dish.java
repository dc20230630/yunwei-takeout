package com.yunwei.pojo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 菜品实体，对应 dish 表。
 */
@Data
public class Dish {
    private Long id;
    private String name;
    //菜品分类ID
    private Long categoryId;
    private BigDecimal price;
    private String image;
    private String description;
    // 1 起售，0 停售
    private Integer status;
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
