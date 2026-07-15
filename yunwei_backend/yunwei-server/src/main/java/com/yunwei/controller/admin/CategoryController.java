package com.yunwei.controller.admin;

import com.yunwei.common.result.PageResult;
import com.yunwei.common.result.Result;
import com.yunwei.pojo.dto.CategoryDTO;
import com.yunwei.pojo.dto.CategoryPageQueryDTO;
import com.yunwei.pojo.entity.Category;
import com.yunwei.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类管理接口。
 */
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
@Tag(name = "分类管理")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 新增分类。
     */
    @PostMapping
    @Operation(summary = "新增分类")
    public Result<Void> save(@RequestBody @Valid CategoryDTO categoryDTO) {
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类。
     */
    @GetMapping("/page")
    @Operation(summary = "分类分页查询")
    public Result<PageResult<Category>> page(@Valid CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult<Category> pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类。
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteById(@PathVariable Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类名称、类型和排序。
     */
    @PutMapping
    @Operation(summary = "修改分类")
    public Result<Void> update(@RequestBody @Valid CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用或禁用分类。
     */
    @PutMapping("/status")
    @Operation(summary = "启用或禁用分类")
    public Result<Void> updateStatus(
            @RequestParam Long id,
            @RequestParam Integer status) {

        categoryService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 查询已启用的分类，可按菜品或套餐类型筛选。
     */
    @GetMapping("/list")
    @Operation(summary = "查询已启用分类")
    public Result<List<Category>> list(@RequestParam(required = false) Integer type) {
        List<Category> categories = categoryService.list(type);
        return Result.success(categories);
    }
}
