package com.yunwei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching //Spring Cache
public class YunweiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunweiServerApplication.class, args);
    }

}
