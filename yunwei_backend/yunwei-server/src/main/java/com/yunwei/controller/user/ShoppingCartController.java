package com.yunwei.controller.user;


import com.yunwei.common.result.Result;
import com.yunwei.pojo.dto.ShoppingCartDTO;
import com.yunwei.pojo.entity.ShoppingCart;
import com.yunwei.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/shoppingCart")
@Tag(name = "用户端购物车")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @Operation(summary = "添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "查询购物车列表")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartService.list();
        return Result.success(list);
    }

    @PostMapping("/sub/{cartId}")
    @Operation(summary = "减少购物车商品数量")
    public Result sub(@PathVariable Long cartId){
        shoppingCartService.sub(cartId);
        return Result.success();
    }


    @DeleteMapping("/clean")
    @Operation(summary = "清空当前用户购物车")
    public Result<Void> clean() {
        shoppingCartService.clean();
        return Result.success();
    }
}
