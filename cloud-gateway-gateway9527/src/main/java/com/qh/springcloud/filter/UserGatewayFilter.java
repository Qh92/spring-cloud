package com.qh.springcloud.filter;

import com.qh.springcloud.rule.ICustomRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 用户决定访问具体哪个微服务
 *
 * @author Qh
 * @version 1.0
 * @date 2022-01-03 21:53
 */
@Component
public class UserGatewayFilter implements GlobalFilter, Ordered {

    @Autowired
    private ICustomRule customChooseRule;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //customChooseRule.choose(exchange, chain);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
