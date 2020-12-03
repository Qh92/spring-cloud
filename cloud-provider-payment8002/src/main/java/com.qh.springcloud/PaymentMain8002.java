package com.qh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Author Qh
 * @Date 2020/9/20 18:59
 * @Version 1.0
 */
@SpringBootApplication
@EnableEurekaClient
public class PaymentMain8002 {
    public static void main(String[] args){
        SpringApplication.run(PaymentMain8002.class,args);
    }
}
