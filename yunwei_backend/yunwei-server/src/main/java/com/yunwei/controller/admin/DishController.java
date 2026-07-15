package com.yunwei.controller.admin;

import com.yunwei.common.result.PageResult;
import com.yunwei.common.result.Result;
import com.yunwei.pojo.dto.DishDTO;
import com.yunwei.pojo.dto.DishPageQueryDTO;
import com.yunwei.pojo.dto.DishUpdateDTO;
import com.yunwei.pojo.vo.DishVo;
import com.yunwei.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理接口。
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/dish")
@RestController
@Tag(name="菜品管理")
public class DishController {
    private final DishService dishService;

    /**
     * 新增菜品及其口味。
     */
    @PostMapping
    @Operation(summary = "新增菜品")
    public Result save(@RequestBody @Valid DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询菜品。
     * GET 请求的参数会自动封装到 DishPageQueryDTO。
     */
    @GetMapping("/page")
    @Operation(summary = "菜品分页查询")
    public Result<PageResult<DishVo>> page(@Valid DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询:{}",dishPageQueryDTO);
        PageResult<DishVo> pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 起售或停售菜品。
     */
    @PutMapping("/status")
    @Operation(summary = "起售或停售菜品")
    public Result<Void> updateStatus(
            @RequestParam Long id,
            @RequestParam Integer status) {
        dishService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 删除一个或多个菜品。
     *
     * 请求示例：DELETE /admin/dish?ids=1,2,3
     */
    @DeleteMapping
    @Operation(summary = "批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        dishService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询菜品详情，用于编辑弹框回显。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询菜品详情")
    public Result<DishVo> getById(@PathVariable Long id){
        DishVo dishVo = dishService.getByIdWithFlavor(id);
        return Result.success(dishVo);
    }


    /**
     * 修改菜品及其口味。
     */
    @PutMapping
    @Operation(summary = "修改菜品")
    public Result<Void> update(@RequestBody @Valid DishUpdateDTO dishUpdateDTO) {
        dishService.updateWithFlavor(dishUpdateDTO);
        return Result.success();
    }
}
