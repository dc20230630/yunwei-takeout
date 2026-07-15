package com.yunwei.pojo.vo;

import com.yunwei.pojo.entity.DishFlavor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DishVo {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName; // 联表查询得到的分类名称
    private BigDecimal price;
    private String image;
    private String description;
    private Integer status;
    private LocalDateTime updateTime;

    // 当前菜品的全部口味，用于编辑回显
    private List<DishFlavor> flavors;
}