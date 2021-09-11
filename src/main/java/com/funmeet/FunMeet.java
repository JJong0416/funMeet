package com.funmeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class FunMeet {

    public static void main(String[] args) {
        SpringApplication.run(FunMeet.class, args);
    }

}
