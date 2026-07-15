package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增或修改分类的请求参数。
 */
@Data
public class CategoryDTO {

    // 修改分类时使用，新增时不传
    private Long id;

    // 1 表示菜品分类，2 表示套餐分类
    @NotNull(message = "分类类型不能为空")
    private Integer type;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "排序不能为空")
    private Integer sort;
}
