package com.yunwei.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OrderSubmitVO {
    private Long id;
    private String orderNumber;
    private BigDecimal orderAmount;
    private LocalDateTime orderTime;
}
