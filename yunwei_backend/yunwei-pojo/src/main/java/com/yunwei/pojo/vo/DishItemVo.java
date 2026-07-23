package com.yunwei.pojo.vo;

import lombok.Data;

/**
 * 套餐详情中展示的菜品项。
 */
@Data
public class DishItemVo {

    private String name;

    private Integer copies;

    private String image;

    private String description;
}
