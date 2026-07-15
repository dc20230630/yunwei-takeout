package com.yunwei.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.*;
import com.yunwei.common.exception.BaseException;
import com.yunwei.properties.OssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class AliOssUtil {

    // 读取 application.yaml 中的 yunwei.oss 配置
    private final OssProperties ossProperties;

    /**
     * 上传字节数组到 OSS
     *
     * @param bytes      文件内容
     * @param objectName OSS 中的文件路径和名称
     * @return 文件访问地址
     */
    public String upload(byte[] bytes, String objectName) {
        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint("https://" + ossProperties.getEndpoint())
                .credentialsProvider(new DefaultCredentialProvider(ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret()))
                .clientConfiguration(clientBuilderConfiguration)
                .region(ossProperties.getRegion())
                .build();

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, new ByteArrayInputStream(bytes));

            // 创建PutObject请求。
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            log.error("OSS 上传失败，文件名：{}", objectName, oe);
            throw new BaseException("文件上传失败");
        } finally {
            ossClient.shutdown();
        }
        return "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + objectName;
    }
}