package com.yunwei.controller.admin;

import com.yunwei.common.result.PageResult;
import com.yunwei.common.result.Result;
import com.yunwei.pojo.dto.SetmealDTO;
import com.yunwei.pojo.dto.SetmealPageQueryDTO;
import com.yunwei.pojo.vo.SetmealVo;
import com.yunwei.service.SetmealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
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
 * 管理端套餐管理接口。
 */
@RestController
@RequestMapping("/admin/setmeal")
@RequiredArgsConstructor
@Tag(name = "套餐管理")
public class SetmealController {

    private final SetmealService setmealService;

    /**
     * 新增套餐及其关联菜品。
     */
    @CacheEvict(cacheNames = "setmealCache",key="#setmealDTO.categoryId")
    @PostMapping
    @Operation(summary = "新增套餐")
    public Result<Void> save(@RequestBody @Valid SetmealDTO setmealDTO) {
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询套餐。
     */
    @GetMapping("/page")
    @Operation(summary = "套餐分页查询")
    public Result<PageResult<SetmealVo>> page(@Valid SetmealPageQueryDTO setmealPageQueryDTO) {
        return Result.success(setmealService.pageQuery(setmealPageQueryDTO));
    }

    /**
     * 批量删除停售套餐。
     */
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @DeleteMapping
    @Operation(summary = "批量删除套餐")
    public Result<Void> delete(@RequestParam List<Long> ids) {
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询套餐详情，用于编辑回显。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询套餐详情")
    public Result<SetmealVo> getById(@PathVariable Long id) {
        return Result.success(setmealService.getByIdWithDish(id));
    }

    /**
     * 修改套餐及其关联菜品。
     */
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @PutMapping
    @Operation(summary = "修改套餐")
    public Result<Void> update(@RequestBody @Valid SetmealDTO setmealDTO) {
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 起售或停售套餐。
     */
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @PutMapping("/status")
    @Operation(summary = "起售或停售套餐")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        setmealService.updateStatus(id, status);
        return Result.success();
    }
}
