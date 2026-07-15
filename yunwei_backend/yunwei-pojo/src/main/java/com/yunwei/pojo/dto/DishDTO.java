package com.yunwei.pojo.dto;

import com.yunwei.pojo.entity.DishFlavor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新增菜品时接收前端提交的数据。
 */
@Data
public class DishDTO {
     @NotBlank(message = "菜品名称不能为空")
    private String name;

    @NotNull(message = "菜品分类不能为空")
    private Long categoryId;

    @NotNull(message = "菜品价格不能为空")
    @DecimalMin(value = "0.01", message = "菜品价格必须大于 0")
    private BigDecimal price;   

    @NotBlank(message = "请上传菜品图片")
    private String image;

    private String description;

    // 一个菜品可以有多个口味
    private List<DishFlavor> flavors;
}
