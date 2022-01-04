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
        //hystrix底层使用的是tomcat线程池
        return "线程池：  "+Thread.currentThread().getName()+"   paymentInfo_OK,id: "+id+"  成功";
    }

    /**
     * paymentInfo_Timeout方法出问题了,paymentInfo_TimeoutHandler兜底处理 运行异常与超时异常都会走这个兜底方法
     * 3秒钟以内就是正常的业务逻辑，超过3秒就服务降级
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "paymentInfo_TimeoutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")
    })
    public String paymentInfo_Timeout(Integer id){

        int age = 10 / 0;
        int timeout = 3000;
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：  "+Thread.currentThread().getName()+"   paymentInfo_Timeout,id: "+id+"  成功输出！！！！！！,耗时"+timeout+"毫秒";
    }

    /**
     * 降级（兜底）方法
     * @param id
     * @return
     */
    public String paymentInfo_TimeoutHandler(Integer id){
        return "线程池：  "+Thread.currentThread().getName()+"   系统繁忙或运行报错，请稍后重试,id: "+id+"   ^_^";
    }

    /**
     * 服务熔断
     * 以下配置的意思：在20秒钟内，如果请求10次，失败了超过60%就熔断，不能使用。
     * 休眠10秒后，会将断路器置为半打开状态，尝试熔断的请求命令。如果依然失败就将断路器继续设置为打开状态，如果成功就设置为关闭状态。
     * 配置项：HystrixCommandProperties
     */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),  //是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),   //请求次数
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds",value = "20000"),//滚动时间
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),  //休眠时间窗
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
