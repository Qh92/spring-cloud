package com.qh.springcloud.rule;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.server.ServerWebExchange;

/**
 * 自定义rule
 *
 * @author Qh
 * @version 1.0
 * @date 2022-01-03 22:16
 */
public interface ICustomRule {
    ServiceInstance choose(ServerWebExchange exchange, DiscoveryClient discoveryClient);
}
