package com.yunwei.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.yunwei.properties.WechatProperties;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * 微信支付 APIv3 工具类。
 * 只有启用真实支付接口并配置商户证书后，才会实际访问微信支付平台。
 */
@Component
@RequiredArgsConstructor
public class WeChatPayUtil {

    public static final String JSAPI = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    public static final String REFUNDS = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    private final WechatProperties wechatProperties;
    private final ObjectMapper objectMapper;

    private CloseableHttpClient getClient() throws Exception {
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
                new FileInputStream(new File(wechatProperties.getPrivateKeyFilePath()))
        );
        X509Certificate wechatPayCertificate = PemUtil.loadCertificate(
                new FileInputStream(new File(wechatProperties.getWechatPayCertFilePath()))
        );

        return WechatPayHttpClientBuilder.create()
                .withMerchant(
                        wechatProperties.getMchId(),
                        wechatProperties.getMchSerialNo(),
                        merchantPrivateKey
                )
                .withWechatPay(List.of(wechatPayCertificate))
                .build();
    }

    private JsonNode post(String url, ObjectNode body) throws Exception {
        try (CloseableHttpClient httpClient = getClient()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            httpPost.addHeader("Wechatpay-Serial", wechatProperties.getMchSerialNo());
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(body), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return objectMapper.readTree(responseBody);
            }
        }
    }

    /**
     * 调用 JSAPI 下单接口，返回微信支付平台的原始响应。
     */
    public JsonNode pay(String orderNumber, BigDecimal amount, String description, String openid) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("appid", wechatProperties.getAppId());
        body.put("mchid", wechatProperties.getMchId());
        body.put("description", description);
        body.put("out_trade_no", orderNumber);
        body.put("notify_url", wechatProperties.getNotifyUrl());

        ObjectNode amountNode = body.putObject("amount");
        amountNode.put("total", amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact());
        amountNode.put("currency", "CNY");

        body.putObject("payer").put("openid", openid);
        return post(JSAPI, body);
    }

    /**
     * 根据预支付交易单生成小程序 wx.requestPayment 所需的二次签名参数。
     */
    public JsonNode buildPaymentParams(JsonNode prepayResponse) throws Exception {
        String prepayId = prepayResponse.path("prepay_id").asText();
        if (prepayId.isBlank()) {
            return prepayResponse;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        String packageValue = "prepay_id=" + prepayId;
        String signMessage = wechatProperties.getAppId() + "\n"
                + timeStamp + "\n"
                + nonceStr + "\n"
                + packageValue + "\n";

        PrivateKey privateKey = PemUtil.loadPrivateKey(
                new FileInputStream(new File(wechatProperties.getPrivateKeyFilePath()))
        );
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(signMessage.getBytes(StandardCharsets.UTF_8));

        ObjectNode paymentParams = objectMapper.createObjectNode();
        paymentParams.put("timeStamp", timeStamp);
        paymentParams.put("nonceStr", nonceStr);
        paymentParams.put("package", packageValue);
        paymentParams.put("signType", "RSA");
        paymentParams.put("paySign", Base64.getEncoder().encodeToString(signature.sign()));
        return paymentParams;
    }

    /**
     * 保留课程中的退款能力；当前项目尚未提供退款业务入口。
     */
    public JsonNode refund(String orderNumber, String refundNumber, BigDecimal refund, BigDecimal total)
            throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("out_trade_no", orderNumber);
        body.put("out_refund_no", refundNumber);
        body.put("notify_url", wechatProperties.getRefundNotifyUrl());

        ObjectNode amountNode = body.putObject("amount");
        amountNode.put("refund", refund.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact());
        amountNode.put("total", total.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact());
        amountNode.put("currency", "CNY");
        return post(REFUNDS, body);
    }
}
