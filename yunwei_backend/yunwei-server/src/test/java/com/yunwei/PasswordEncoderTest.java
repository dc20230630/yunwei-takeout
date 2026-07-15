package com.yunwei;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 用于生成初始管理员密码的 BCrypt 哈希。
 */
class PasswordEncoderTest {

    @Test
    void generateAdminPassword() {
        // BCrypt 密码加密工具
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 开发阶段的初始管理员明文密码
        String rawPassword = "123456";

        // 每次生成的哈希都可能不同，但都能校验同一个明文密码
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 在 IDEA 的运行控制台中复制这一行输出的哈希值
        System.out.println("管理员密码 BCrypt 哈希：" + encodedPassword);

        // 验证刚生成的哈希确实能匹配明文密码
        Assertions.assertTrue(
                passwordEncoder.matches(rawPassword, encodedPassword)
        );
    }
}