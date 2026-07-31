package com.yunwei.controller.user;

import com.yunwei.common.result.Result;
import com.yunwei.pojo.dto.OrdersPaymentDTO;
import com.yunwei.pojo.dto.OrdersSubmitDTO;
import com.yunwei.pojo.vo.OrderSubmitVO;
import com.yunwei.pojo.vo.OrderVO;
import com.yunwei.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/order")
@RequiredArgsConstructor
@Tag(name="用户端订单管理")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/submit")
    @Operation(summary = "提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody @Valid OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/mock-payment")
    @Operation(summary = "模拟支付订单")
    public Result payment(@RequestBody @Valid OrdersPaymentDTO ordersPaymentDTO) {
        orderService.mockPayment(ordersPaymentDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "查询当前用户订单列表")
    public Result<List<OrderVO>> list() {
        return Result.success(orderService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询当前用户订单详情")
    public Result<OrderVO> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }


    @PutMapping("/{id}/urge")
    public Result<Void> UrgeOrder(@PathVariable("id") Long id){
        orderService.urgeOrder(id);
        return Result.success();
    }
}
