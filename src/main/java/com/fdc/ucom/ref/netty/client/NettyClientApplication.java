package com.fdc.ucom.ref.netty.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by fbqgez6 - Omar  on 7/6/17.
 */
@SpringBootApplication
@EnableAsync
public class NettyClientApplication {

    public static void main(String... args) {
        SpringApplication.run(NettyClientApplication.class, args);
    }
}
