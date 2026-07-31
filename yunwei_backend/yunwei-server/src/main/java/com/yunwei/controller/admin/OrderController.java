package com.yunwei.controller.admin;

import com.yunwei.common.result.Result;
import com.yunwei.pojo.dto.OrderAdminQueryDTO;
import com.yunwei.pojo.vo.OrderVO;
import com.yunwei.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端订单管理接口。
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@RequiredArgsConstructor
@Tag(name = "管理端订单管理")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/list")
    @Operation(summary = "查询订单列表")
    public Result<List<OrderVO>> list(OrderAdminQueryDTO orderAdminQueryDTO) {
        return Result.success(orderService.listForAdmin(orderAdminQueryDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询订单详情")
    public Result<OrderVO> getById(@PathVariable Long id) {
        return Result.success(orderService.getByIdForAdmin(id));
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "接单")
    public Result<Void> acceptOrder(@PathVariable Long id) {
        orderService.acceptOrder(id);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }
}
