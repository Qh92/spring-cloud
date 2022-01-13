package com.qinh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author Qh
 * @version 1.0
 * @date 2022-01-13 22:37
 */
@SpringBootApplication
@EnableZuulProxy
public class ZuulMain9528 {

    public static void main(String[] args) {
        SpringApplication.run(ZuulMain9528.class, args);
    }
}
