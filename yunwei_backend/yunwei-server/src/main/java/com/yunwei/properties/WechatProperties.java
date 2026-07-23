package com.yunwei.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取小程序微信配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "yunwei.wechat")
public class WechatProperties {

    /**
     * 小程序 AppID。
     */
    private String appId;

    /**
     * 小程序 AppSecret，只能保存在后端。
     */
    private String appSecret;

    /**
     * 微信支付商户号。
     */
    private String mchId;

    /**
     * 商户 API 证书序列号。
     */
    private String mchSerialNo;

    /**
     * 商户私钥文件路径。
     */
    private String privateKeyFilePath;

    /**
     * 微信支付 APIv3 密钥。
     */
    private String apiV3Key;

    /**
     * 微信支付平台证书文件路径。
     */
    private String wechatPayCertFilePath;

    /**
     * 支付成功回调地址。
     */
    private String notifyUrl;

    /**
     * 退款成功回调地址。
     */
    private String refundNotifyUrl;
}
