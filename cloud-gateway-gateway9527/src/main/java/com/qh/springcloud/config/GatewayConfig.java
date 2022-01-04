package com.qh.springcloud.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    /**
     * 通过9527网关访问到外网的百度新闻网址
     * 配置一个id为route-name的路由规则，当访问地址http://localhost:9527/guonei时会自动转发到地址http://news.baidu.com/guonei
     * @param routeLocatorBuilder
     * @return
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder){
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();

        routes.route("path_route_springcloud",r ->r.path("/guonei").uri("http://news.baidu.com/guonei")).build();
        return routes.build();
    }


    /*@Bean
    public KeyResolver hostAddrKeyResolver() {
        //按URL限流,即以每秒内请求数按URL分组统计，超出限流的url请求都将返回429状态
        return exchange -> {
            System.out.println("hostAddrKeyResolver方法调用");
            return Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
        };
    }*/


    @Bean
    public KeyResolver userKeyResolver() {
        //按用户限流
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));
    }

    /*@Bean
    public KeyResolver ipKeyResolver() {
        //按IP来限流
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }*/
}
