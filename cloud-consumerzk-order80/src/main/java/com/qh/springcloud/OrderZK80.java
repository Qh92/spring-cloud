package com.qh.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //该注解用于使用consul或者zookeeper作为注册中心注册服务
public class OrderZK80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderZK80.class,args);
    }

}
