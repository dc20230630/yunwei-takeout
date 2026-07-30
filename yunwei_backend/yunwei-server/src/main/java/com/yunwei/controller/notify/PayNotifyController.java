package com.yunwei.controller.notify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.yunwei.properties.WechatProperties;
import com.yunwei.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 微信支付成功回调。
 * 未启用真实支付时不会注册，避免在没有商户配置时接收任何回调。
 */
@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "yunwei.wechat.pay", name = "enabled", havingValue = "true")
public class PayNotifyController {

    private final OrderService orderService;
    private final WechatProperties wechatProperties;
    private final ObjectMapper objectMapper;

    @PostMapping("/paySuccess")
    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String plainText = decryptData(readData(request));
        JsonNode paymentData = objectMapper.readTree(plainText);
        String orderNumber = paymentData.path("out_trade_no").asText();

        orderService.paySuccess(orderNumber);
        responseToWeixin(response);
    }

    private String readData(HttpServletRequest request) throws Exception {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    private String decryptData(String body) throws Exception {
        JsonNode resource = objectMapper.readTree(body).path("resource");
        AesUtil aesUtil = new AesUtil(wechatProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        return aesUtil.decryptToString(
                resource.path("associated_data").asText().getBytes(StandardCharsets.UTF_8),
                resource.path("nonce").asText().getBytes(StandardCharsets.UTF_8),
                resource.path("ciphertext").asText()
        );
    }

    private void responseToWeixin(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(
                objectMapper.writeValueAsBytes(Map.of("code", "SUCCESS", "message", "SUCCESS"))
        );
        response.flushBuffer();
    }
}
