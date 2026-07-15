package com.yunwei.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yunwei.oss")
public class OssProperties {
    // OSS 地域节点，不带 https://
    private String endpoint;

    // OSS Bucket 名称
    private String bucketName;

    // 访问密钥 ID
    private String accessKeyId;

    // 访问密钥 Secret
    private String accessKeySecret;

    // Bucket 所在地域，例如 cn-beijing
    private String region;
}
