package com.yunwei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching //Spring Cache
@EnableScheduling //开启Spring的请示任务能力
public class YunweiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunweiServerApplication.class, args);
    }

}
