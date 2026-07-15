package com.yunwei.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密相关配置。
 */

@Configuration
public class PasswordConfig {
    /**
     * 把 BCrypt 密码加密工具交给 Spring 管理。
     *
     * 后续在 Service 中可以直接注入 PasswordEncoder 使用。
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
