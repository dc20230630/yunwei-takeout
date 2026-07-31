package com.yunwei.service;

import com.yunwei.pojo.dto.OrderAdminQueryDTO;
import com.yunwei.pojo.dto.OrdersSubmitDTO;
import com.yunwei.pojo.dto.OrdersPaymentDTO;
import com.yunwei.pojo.vo.OrderPaymentVO;
import com.yunwei.pojo.vo.OrderSubmitVO;
import com.yunwei.pojo.vo.OrderVO;
import jakarta.validation.Valid;

import java.util.List;

public interface OrderService {
    OrderSubmitVO submit(@Valid OrdersSubmitDTO ordersSubmitDTO);

    void mockPayment(@Valid OrdersPaymentDTO ordersPaymentDTO);

    OrderPaymentVO payment(@Valid OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    void paySuccess(String orderNumber);

    List<OrderVO> list();

    OrderVO getById(Long id);

    List<OrderVO> listForAdmin(OrderAdminQueryDTO orderAdminQueryDTO);

    OrderVO getByIdForAdmin(Long id);

    void acceptOrder(Long id);

    void cancelOrder(Long id);

    /**
     * 取消超过支付时限且仍未支付的订单。
     *
     * @return 本次自动取消的订单数量
     */
    int cancelTimeOutOrder();

    void urgeOrder(Long id);
}
