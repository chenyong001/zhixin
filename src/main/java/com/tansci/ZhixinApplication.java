package com.tansci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ZhixinApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhixinApplication.class, args);
    }

}
