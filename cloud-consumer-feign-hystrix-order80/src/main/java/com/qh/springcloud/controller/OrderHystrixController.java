package com.qh.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.qh.springcloud.service.PaymentHystrixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@DefaultProperties(defaultFallback = "payment_global_fallbackMethod") //全局兜底方法
public class OrderHystrixController {
    @Resource
    private PaymentHystrixService paymentHystrixService;

    @GetMapping(value = "/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id")  Integer id){
        String result = paymentHystrixService.paymentInfo_OK(id);
        log.info("*************result："+result);
        return result;
    }


    @GetMapping(value = "/consumer/payment/hystrix/timeout/{id}")
    /*@HystrixCommand(fallbackMethod = "paymentInfo_TimeoutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1500")  //6秒钟以内就是正常的业务逻辑
    })*/
    @HystrixCommand
    public String paymentInfo_Timeout(@PathVariable("id")  Integer id){
        int age = 10 / 0;
        String result = paymentHystrixService.paymentInfo_Timeout(id);
        log.info("*************result："+result);
        return result;
    }


    public String paymentInfo_TimeoutHandler(@PathVariable("id")  Integer id){
        return "我是消费者80,对方支付系统繁忙请10秒后再试或者自己运行出错请检查自己   ^_^";
    }

    /**
     * 全局降级方法，配合@DefaultProperties注解使用
     * @return
     */
    public String payment_global_fallbackMethod(){
        return "global异常处理信息，请稍后再试  ^_^";
    }


}
