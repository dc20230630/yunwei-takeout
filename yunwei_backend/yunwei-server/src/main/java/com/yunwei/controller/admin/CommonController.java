package com.yunwei.controller.admin;

import com.yunwei.common.result.Result;
import com.yunwei.utils.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Tag(name = "文件上传")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 上传菜品图片到阿里云 OSS。
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("上传文件：{}", file.getOriginalFilename());

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();

        // 获取原文件的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 生成日期目录，例如 2026/07/16
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 构造新文件名称
        // 生成 OSS 文件路径
        String objectName = "dish/" + datePath + "/" + UUID.randomUUID() + suffix;

        // 获取新的路径
        String path = aliOssUtil.upload(file.getBytes(), objectName);

        return Result.success(path);
    }
}
