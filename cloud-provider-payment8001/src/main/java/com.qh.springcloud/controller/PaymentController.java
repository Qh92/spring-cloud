package com.qh.springcloud.controller;

import com.qh.springcloud.entities.CommonResult;
import com.qh.springcloud.entities.Payment;
import com.qh.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author Qh
 * @Date 2020/9/20 20:34
 * @Version 1.0
 */
@RestController
@Slf4j
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    /**
     * 对于注册进eureka里面的微服务，可以通过服务发现来获得该服务的信息
     */
    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping(value = "/payment/create")
    public CommonResult create (@RequestBody Payment payment){//@RequestBody,将请求体中的数据映射为对应的对象
        System.out.println(payment);
        int result = paymentService.create(payment);
        log.info("插入的结果: "+result);
        if(result > 0){
            return new CommonResult<>(200, "插入数据成功,serverPort:" + serverPort, result);
        }else{
            return new CommonResult<>(444,"插入数据失败",null);
        }
    };

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById (@PathVariable("id") Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("查询成功:"+payment);
        if(payment != null){
            return new CommonResult<>(200,"查询数据成功,serverPort:"+serverPort,payment);
        }else{
            return new CommonResult<>(444,"查询数据失败，查询id"+id,null);
        }
    };

    //服务发现
    @GetMapping(value = "/payment/discovery")
    public Object discoveryServices(){
        //查看eureka上注册了多少服务，服务列表清单
        List<String> services = discoveryClient.getServices();
        for (String element : services){
            log.info("***************** service:"+element);
        }

        //查看某个服务的具体信息，通过微服务的名称获取具体信息
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances){

            log.info("scheme:"+instance.getScheme()+" instanceId : " + instance.getInstanceId() +" serviceId : "+instance.getServiceId()+" host: "+instance.getHost()+" uri: "+instance.getUri()+" port : "+instance.getPort());
        }
        return this.discoveryClient;
    }

    @GetMapping(value = "/payment/lb")
    public String getPaymentLB(){
        return serverPort;
    };

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout(){
        //暂停几秒钟线程
        try{
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return serverPort;

    }

    @GetMapping("/payment/zipkin")
    public String paymentZipkin() {
        return "hi ,i'am paymentzipkin server fall back，welcome to atguigu，O(∩_∩)O哈哈~";
    }
}
