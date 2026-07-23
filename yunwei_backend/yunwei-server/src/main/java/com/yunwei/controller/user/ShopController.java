package com.yunwei.controller.user;

import com.yunwei.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


/**
 * 用户端店铺营业状态接口。
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Tag(name = "用户端店铺")
public class ShopController {
    private static final String KEY = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询店铺营业状态。
     */
    @GetMapping("/status")
    @Operation(summary = "查询店铺营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("查询店铺营业状态：{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
