package com.yunwei.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunwei.common.exception.BaseException;
import com.yunwei.context.BaseContext;
import com.yunwei.mapper.AddressBookMapper;
import com.yunwei.mapper.OrderDetailMapper;
import com.yunwei.mapper.OrderMapper;
import com.yunwei.mapper.ShoppingCartMapper;
import com.yunwei.mapper.UserMapper;
import com.yunwei.pojo.dto.OrdersPaymentDTO;
import com.yunwei.pojo.dto.OrdersSubmitDTO;
import com.yunwei.pojo.dto.OrderAdminQueryDTO;
import com.yunwei.pojo.entity.AddressBook;
import com.yunwei.pojo.entity.OrderDetail;
import com.yunwei.pojo.entity.Orders;
import com.yunwei.pojo.entity.ShoppingCart;
import com.yunwei.pojo.entity.User;
import com.yunwei.pojo.vo.OrderPaymentVO;
import com.yunwei.pojo.vo.OrderSubmitVO;
import com.yunwei.pojo.vo.OrderVO;
import com.yunwei.service.OrderService;
import com.yunwei.utils.WeChatPayUtil;
import com.yunwei.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final AddressBookMapper addressBookMapper;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final UserMapper userMapper;
    private final WeChatPayUtil weChatPayUtil;
    private final WebSocketServer webSocketServer;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        //查询当前登录用户的购物车，不能使用前端传来的商品和价格
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(userId);
        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new BaseException("购物车为空");
        }
        //地址必须属于当前登录用户
        AddressBook addressBook = addressBookMapper.getByIdAndUserId(ordersSubmitDTO.getAddressBookId(), userId);
        if (addressBook == null) {
            throw new BaseException("收货地址不存在");
        }
        // 订单金额必须由后端按购物车重新计算，不能使用前端传来的总价
        // item.getAmount() 是当前商品单价，item.getNumber() 是当前商品购买数量。
        // map 会把每条购物车记录转换为“单价 × 数量”的小计，例如 12.50 元购买 2 份得到 25.00 元。
        // reduce 从 0 元开始，将所有商品小计逐个相加，最后得到订单的商品总金额。
        // 只有预约配送时才要求填写送达时间
        if (Integer.valueOf(0).equals(ordersSubmitDTO.getDeliveryStatus())
                && ordersSubmitDTO.getDeliveryTime() == null) {
            throw new BaseException("预约配送请选择送达时间");
        }
        // 只有自选餐具时才要求填写餐具数量
        if (Integer.valueOf(0).equals(ordersSubmitDTO.getTablewareStatus())
                && ordersSubmitDTO.getTablewareNumber() == null) {
            throw new BaseException("自选餐具请输入餐具数量");
        }

        BigDecimal amount = shoppingCarts.stream().map(item -> item.getAmount().multiply(BigDecimal.valueOf(item.getNumber()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(1);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(0);
        orders.setAmount(amount);

        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());

        orderMapper.insert(orders);


        List<OrderDetail> orderDetails = shoppingCarts.stream().map(item -> {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setImage(item.getImage());
            detail.setOrderId(orders.getId());
            detail.setDishId(item.getDishId());
            detail.setSetmealId(item.getSetmealId());
            detail.setDishFlavor(item.getDishFlavor());
            detail.setNumber(item.getNumber());
            detail.setAmount(item.getAmount());
            return detail;
        }).toList();
        orderDetailMapper.insertBatch(orderDetails);

        //订单和明细保存成功后，清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

    }

    @Override
    public void mockPayment(OrdersPaymentDTO ordersPaymentDTO) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getByNumberAndUserId(ordersPaymentDTO.getOrderNumber(), userId);
        if (orders == null) {
            throw new BaseException("订单不存在");
        }
        if (Integer.valueOf(1).equals(orders.getPayStatus())) {
            throw new BaseException("订单已支付");
        }
        if (!Integer.valueOf(1).equals(orders.getStatus())) {
            // 定时任务已取消的订单不能再进入模拟支付流程
            throw new BaseException("订单已取消，不能支付");
        }

        // 当前未接入真实微信商户支付，这里模拟支付成功后订单应进入的状态
        orders.setStatus(2); // 待接单
        orders.setPayStatus(1); // 已支付
        orders.setCheckoutTime(LocalDateTime.now());

        // 更新条件限制为未支付，避免两个请求同时把同一笔订单重复支付
        int updatedRows = orderMapper.updatePaymentStatus(orders);
        if (updatedRows == 0) {
            throw new BaseException("订单已支付");
        }
        //只有订单状态确实从"待支付"更新为"待接单"后,才通知管理端
        //这样重复支付请求不会产生重复来电提醒
        webSocketServer.sendToAllClient("NEW_ORDER");
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getByNumberAndUserId(ordersPaymentDTO.getOrderNumber(), userId);
        if (orders == null) {
            throw new BaseException("订单不存在");
        }
        if (Integer.valueOf(1).equals(orders.getPayStatus())) {
            throw new BaseException("订单已支付");
        }

        User user = userMapper.getById(userId);
        JsonNode prepayResponse = weChatPayUtil.pay(
                orders.getNumber(),
                orders.getAmount(),
                "云味外卖订单",
                user.getOpenid()
        );
        if ("ORDERPAID".equals(prepayResponse.path("code").asText())) {
            throw new BaseException("订单已支付");
        }

        JsonNode paymentParams = weChatPayUtil.buildPaymentParams(prepayResponse);
        if (paymentParams.path("prepay_id").isMissingNode()) {
            throw new BaseException(prepayResponse.path("message").asText());
        }

        OrderPaymentVO orderPaymentVO = new OrderPaymentVO();
        orderPaymentVO.setTimeStamp(paymentParams.path("timeStamp").asText());
        orderPaymentVO.setNonceStr(paymentParams.path("nonceStr").asText());
        orderPaymentVO.setPackageStr(paymentParams.path("package").asText());
        orderPaymentVO.setSignType(paymentParams.path("signType").asText());
        orderPaymentVO.setPaySign(paymentParams.path("paySign").asText());
        return orderPaymentVO;
    }

    @Override
    public void paySuccess(String orderNumber) {
        Orders orders = orderMapper.getByNumber(orderNumber);
        if (orders == null) {
            throw new BaseException("订单不存在");
        }
        if (Integer.valueOf(1).equals(orders.getPayStatus())) {
            return;
        }

        orders.setStatus(2); // 待接单
        orders.setPayStatus(1); // 已支付
        orders.setCheckoutTime(LocalDateTime.now());
        int updateRows = orderMapper.updatePaymentStatus(orders);
        if (updateRows == 1) {
            //微信支付回调可能重复触发,只有首次更新成功才推送
            webSocketServer.sendToAllClient("NEW_ORDER");
        }
    }

    @Override
    public List<OrderVO> list() {
        Long userId = BaseContext.getCurrentId();
        return toOrderVOList(orderMapper.listByUserId(userId));
    }

    @Override
    public List<OrderVO> listForAdmin(OrderAdminQueryDTO orderAdminQueryDTO) {
        return toOrderVOList(orderMapper.listForAdmin(orderAdminQueryDTO));
    }

    private List<OrderVO> toOrderVOList(List<Orders> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream().map(Orders::getId).toList();
        List<OrderDetail> orderDetails = orderDetailMapper.listByOrderIds(orderIds);
        Map<Long, List<OrderDetail>> detailsByOrderId = new HashMap<>();
        for (Long orderId : orderIds) {
            detailsByOrderId.put(orderId, new ArrayList<>());
        }
        for (OrderDetail orderDetail : orderDetails) {
            detailsByOrderId.get(orderDetail.getOrderId()).add(orderDetail);
        }

        return orders.stream()
                .map(order -> toOrderVO(order, detailsByOrderId.get(order.getId())))
                .toList();
    }

    @Override
    public OrderVO getById(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders order = orderMapper.getByIdAndUserId(id, userId);
        if (order == null) {
            throw new BaseException("订单不存在");
        }

        List<OrderDetail> orderDetails = orderDetailMapper.listByOrderIds(List.of(id));
        return toOrderVO(order, orderDetails);
    }

    @Override
    public OrderVO getByIdForAdmin(Long id) {
        Orders order = orderMapper.getById(id);
        if (order == null) {
            throw new BaseException("订单不存在");
        }

        List<OrderDetail> orderDetails = orderDetailMapper.listByOrderIds(List.of(id));
        return toOrderVO(order, orderDetails);
    }

    @Override
    public void acceptOrder(Long id) {
        if (orderMapper.acceptOrder(id) == 0) {
            throw new BaseException("订单当前状态不能接单");
        }
    }

    @Override
    public void cancelOrder(Long id) {
        if (orderMapper.cancelOrder(id, LocalDateTime.now()) == 0) {
            throw new BaseException("订单当前状态不能取消");
        }
    }

    @Override
    public int cancelTimeOutOrder() {
        LocalDateTime now = LocalDateTime.now();

        //超过15分钟仍未支付的待付款订单自动取消
        LocalDateTime deadline = now.minusMinutes(15);

        return orderMapper.cancelTimeoutOrders(deadline, now);
    }

    @Override
    public void urgeOrder(Long id) {
        Long userId = BaseContext.getCurrentId();
        // 只能催当前登录用户自己的订单
        Orders order = orderMapper.getByIdAndUserId(id, userId);
        if (order == null) {
            throw new BaseException("订单不存在");
        }

        // 只有待接单状态才能催商家
        if (!Integer.valueOf(2).equals(order.getStatus())) {
            throw new BaseException("当前订单不能催单");
        }
        // 催单不改变订单状态，只通知在线管理端，并带上订单信息供管理端定位。
        Map<String, Object> reminder = new HashMap<>();
        reminder.put("type", "URGE_ORDER");
        reminder.put("orderId", order.getId());
        reminder.put("orderNumber", order.getNumber());
        reminder.put("consignee", order.getConsignee());
        reminder.put("phone", order.getPhone());

        try {
            webSocketServer.sendToAllClient(objectMapper.writeValueAsString(reminder));
        } catch (JsonProcessingException e) {
            throw new BaseException("催单提醒发送失败");
        }
    }

    private OrderVO toOrderVO(Orders order, List<OrderDetail> orderDetails) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetails(orderDetails);
        return orderVO;
    }
}
