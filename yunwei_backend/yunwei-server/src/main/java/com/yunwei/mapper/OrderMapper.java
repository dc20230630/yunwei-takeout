package com.yunwei.mapper;

import com.yunwei.pojo.dto.OrderAdminQueryDTO;
import com.yunwei.pojo.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    Orders getByNumber(@Param("number") String number);

    Orders getByNumberAndUserId(@Param("number") String number, @Param("userId") Long userId);

    List<Orders> listByUserId(Long userId);

    Orders getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    List<Orders> listForAdmin(OrderAdminQueryDTO orderAdminQueryDTO);

    Orders getById(Long id);

    int acceptOrder(Long id);

    int cancelOrder(@Param("id") Long id, @Param("cancelTime") LocalDateTime cancelTime);

    int updatePaymentStatus(Orders orders);

    int cancelTimeoutOrders(@Param("deadline") LocalDateTime deadline, @Param("cancelTime") LocalDateTime cancelTime);
}
