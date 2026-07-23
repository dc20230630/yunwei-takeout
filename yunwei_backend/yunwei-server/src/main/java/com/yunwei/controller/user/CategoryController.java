package com.yunwei.controller.user;

import com.yunwei.common.result.Result;
import com.yunwei.pojo.entity.Category;
import com.yunwei.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端分类浏览接口。
 */
@RestController("userCategoryController")
@RequestMapping("/user/category")
@RequiredArgsConstructor
@Tag(name = "用户端分类")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 查询已启用的菜品或套餐分类。
     */
    @GetMapping("/list")
    @Operation(summary = "查询分类")
    public Result<List<Category>> list(@RequestParam(required = false) Integer type) {
        return Result.success(categoryService.list(type));
    }
}
