package com.yunwei.controller.user;

import com.yunwei.common.result.Result;
import com.yunwei.constant.StatusConstant;
import com.yunwei.pojo.entity.Setmeal;
import com.yunwei.pojo.vo.DishItemVo;
import com.yunwei.service.SetmealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端套餐浏览接口。
 */
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@RequiredArgsConstructor
@Tag(name = "用户端套餐")
public class SetmealController {

    private final SetmealService setmealService;

    /**
     * 查询指定分类下正在起售的套餐。
     */
    @GetMapping("/list")
    @Cacheable(cacheNames="setmealCache",key="#categoryId")
    @Operation(summary = "根据分类查询套餐")
    public Result<List<Setmeal>> list(@RequestParam Long categoryId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLED);

        return Result.success(setmealService.list(setmeal));
    }

    /**
     * 查询套餐包含的菜品。
     */
    @GetMapping("/dish/{id}")
    @Operation(summary = "根据套餐查询菜品")
    public Result<List<DishItemVo>> dishList(@PathVariable Long id) {
        return Result.success(setmealService.getDishItemById(id));
    }
}
