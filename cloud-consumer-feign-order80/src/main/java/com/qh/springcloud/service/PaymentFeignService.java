package com.qh.springcloud.service;

import com.qh.springcloud.entities.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
//微服务名称，找哪个具体的微服务
@FeignClient(value = "CLOUD-PAYMENT-SERVICE")
public interface PaymentFeignService {
    /**
     * 调用对应的微服务的对外暴露的方法
     */
    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById (@PathVariable("id") Long id);

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout();
}
