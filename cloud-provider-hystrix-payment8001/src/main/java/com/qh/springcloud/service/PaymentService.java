package com.qh.springcloud.service;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {
    public String paymentInfo_OK(Integer id){
        return "线程池：  "+Thread.currentThread().getName()+"   paymentInfo_OK,id: "+id+"  成功";
    }

    @HystrixCommand(fallbackMethod = "paymentInfo_TimeoutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")  //3秒钟以内就是正常的业务逻辑
    })
    //paymentInfo_Timeout方法出问题了,paymentInfo_TimeoutHandler兜底处理 运行异常与超时异常都会走这个兜底方法
    public String paymentInfo_Timeout(Integer id){

        //int age = 10 / 0;
        int timeout = 3000;
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：  "+Thread.currentThread().getName()+"   paymentInfo_Timeout,id: "+id+"  成功输出！！！！！！,耗时"+timeout+"毫秒";
    }

    public String paymentInfo_TimeoutHandler(Integer id){
        return "线程池：  "+Thread.currentThread().getName()+"   系统繁忙，请稍后重试,id: "+id+"   ^_^";
    }

    //服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),  //是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),   //请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),  //时间范围
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"), //失败率达到多少后跳闸
    })
    public String paymentCircuitBreaker( Integer id){
        if (id < 0){
            throw new RuntimeException("*****id 不能负数");
        }
        String serialNumber = IdUtil.simpleUUID();

        return Thread.currentThread().getName()+"\t"+"调用成功,流水号："+serialNumber;
    }
    public String paymentCircuitBreaker_fallback( Integer id){
        return "id 不能负数，请稍候再试,(┬＿┬)/~~     id: " +id;
    }


}
