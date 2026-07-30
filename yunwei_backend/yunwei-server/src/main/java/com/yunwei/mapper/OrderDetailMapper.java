package com.yunwei.mapper;

import com.yunwei.pojo.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    void insertBatch(List<OrderDetail> orderDetails);

    List<OrderDetail> listByOrderIds(@Param("orderIds") List<Long> orderIds);
}
