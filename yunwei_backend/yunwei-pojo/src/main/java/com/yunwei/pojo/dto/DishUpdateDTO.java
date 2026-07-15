package com.yunwei.pojo.dto;

import com.yunwei.pojo.entity.DishFlavor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 修改菜品时前端提交的数据。
 */
@Data
public class DishUpdateDTO {

    // 要修改的菜品主键
    @NotNull(message = "菜品 id 不能为空")
    private Long id;

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

    // 修改后该菜品全部的口味数据
    private List<DishFlavor> flavors;
}