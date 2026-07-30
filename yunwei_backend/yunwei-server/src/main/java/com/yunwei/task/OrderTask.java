package com.yunwei.task;

import com.yunwei.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTask {
    private final OrderService orderService;

    /**
     * 每分钟检查一次超时未支付订单。
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void processTimeoutOrder() {
        int cancelledCount = orderService.cancelTimeOutOrder();
        if (cancelledCount > 0) {
            log.info("自动取消{}个超时未支付订单", cancelledCount);
        }
    }
}
