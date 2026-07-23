package com.yunwei.pojo.vo;

import com.yunwei.pojo.entity.SetmealDish;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端套餐展示数据。
 */
@Data
public class SetmealVo {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private BigDecimal price;
    private Integer status;
    private String description;
    private String image;
    private LocalDateTime updateTime;
    private List<SetmealDish> setmealDishes;
}
