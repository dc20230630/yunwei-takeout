package com.yunwei.controller.user;

import com.yunwei.common.result.Result;
import com.yunwei.pojo.dto.OrdersPaymentDTO;
import com.yunwei.pojo.vo.OrderPaymentVO;
import com.yunwei.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 真实微信预支付接口。
 * 未设置 yunwei.wechat.pay.enabled=true 时，这个控制器不会注册。
 */
@RestController
@RequestMapping("/user/order")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "yunwei.wechat.pay", name = "enabled", havingValue = "true")
public class OrderPaymentController {

    private final OrderService orderService;

    @PutMapping("/payment")
    @Operation(summary = "生成微信预支付参数")
    public Result<OrderPaymentVO> payment(@RequestBody @Valid OrdersPaymentDTO ordersPaymentDTO)
            throws Exception {
        return Result.success(orderService.payment(ordersPaymentDTO));
    }
}
