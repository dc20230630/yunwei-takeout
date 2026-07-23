package com.yunwei.pojo.dto;

import com.yunwei.pojo.entity.SetmealDish;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新增或修改套餐时接收的数据。
 */
@Data
public class SetmealDTO {

    private Long id;

    @NotNull(message = "套餐分类不能为空")
    private Long categoryId;

    @NotBlank(message = "套餐名称不能为空")
    private String name;

    @NotNull(message = "套餐价格不能为空")
    @DecimalMin(value = "0.01", message = "套餐价格必须大于 0")
    private BigDecimal price;

    @NotNull(message = "套餐状态不能为空")
    private Integer status;

    private String description;

    @NotBlank(message = "请上传套餐图片")
    private String image;

    @NotEmpty(message = "套餐至少需要关联一道菜品")
    private List<SetmealDish> setmealDishes;
}
