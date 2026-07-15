package com.yunwei.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜品和套餐分类对应的数据库实体。
 */
@Data
public class Category {

    private Long id;

    // 1 表示菜品分类，2 表示套餐分类
    private Integer type;

    private String name;

    private Integer sort;

    // 1 表示启用，0 表示禁用
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
