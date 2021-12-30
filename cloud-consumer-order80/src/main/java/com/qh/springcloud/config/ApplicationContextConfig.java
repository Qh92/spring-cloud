package com.qh.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContextConfig {

    @Bean
    @LoadBalanced //如果自己写了负载均衡算法，要把Ribbon的LoadBalanced注解注释掉
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}
