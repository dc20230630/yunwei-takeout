package com.yunwei.controller.user;

import com.yunwei.common.result.Result;
import com.yunwei.constant.StatusConstant;
import com.yunwei.pojo.entity.Dish;
import com.yunwei.pojo.vo.DishVo;
import com.yunwei.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端菜品浏览接口。
 */
@SuppressWarnings("unchecked")
@RestController("userDishController")
@RequestMapping("/user/dish")
@RequiredArgsConstructor
@Tag(name = "用户端菜品")
public class DishController {

    private final DishService dishService;
    private final RedisTemplate redisTemplate;

    /**
     * 查询指定分类下正在起售的菜品和口味。
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类查询菜品")
    public Result<List<DishVo>> list(@RequestParam Long categoryId) {
        String key = "dish_" + categoryId;
        // 先从redis读取缓存
        List<DishVo> list = (List<DishVo>) redisTemplate.opsForValue().get(key);
        if (list != null) {
            return Result.success(list);
        }

        // 没有就查数据库
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLED);
        list = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }
}
