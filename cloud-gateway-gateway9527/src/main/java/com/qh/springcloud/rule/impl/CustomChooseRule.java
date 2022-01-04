package com.qh.springcloud.rule.impl;

import com.qh.springcloud.rule.ICustomRule;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;


/**
 * @author Qh
 * @version 1.0
 * @date 2022-01-03 22:16
 */
@Component
public class CustomChooseRule implements ICustomRule {
    @Override
    public ServiceInstance choose(ServerWebExchange exchange, DiscoveryClient discoveryClient) {

        //获取并解析token
        //String token = exchange.getRequest().getQueryParams().getFirst("token");
        String username = exchange.getRequest().getQueryParams().getFirst("username");

        List<ServiceInstance> instances = discoveryClient.getInstances("cloud-order-service");
        if (!StringUtils.isEmpty(username)) {
            for (ServiceInstance instance : instances) {
                String name = instance.getMetadata().get("name");
                if (!username.equals(name)) {
                    return instance;
                }
            }
        }else {
            for (ServiceInstance instance : instances) {
                String name = instance.getMetadata().get("name");
                if (username.equals(name)) {
                    return instance;
                }
            }
        }

        return null;
    }
}
