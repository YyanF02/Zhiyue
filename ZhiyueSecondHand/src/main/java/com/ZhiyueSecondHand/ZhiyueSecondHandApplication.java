package com.ZhiyueSecondHand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.ZhiyueSecondHand.mapper")
@EnableScheduling
public class ZhiyueSecondHandApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiyueSecondHandApplication.class, args);
    }

}
