package com.yunwei.mapper;

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

    int updatePaymentStatus(Orders orders);

    int cancelTimeoutOrders(@Param("deadline") LocalDateTime deadline, @Param("cancelTime") LocalDateTime cancelTime);
}
