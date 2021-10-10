package com.qh.springcloud.controller;

import com.qh.springcloud.entities.CommonResult;
import com.qh.springcloud.entities.Payment;
import com.qh.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @PostMapping(value = "/payment/create")
    public CommonResult create (@RequestBody Payment payment){//@RequestBody
        int result = paymentService.create(payment);
        log.info("插入的结果: "+result);
        if(result > 0){
            return new CommonResult(200,"插入数据成功,serverPort:"+serverPort,result);
        }else{
            return new CommonResult(444,"插入数据失败",null);
        }
    };

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById (@PathVariable("id") Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("查询成功:"+payment);
        if(payment != null){
            return new CommonResult(200,"查询数据成功,serverPort:"+serverPort,payment);
        }else{
            return new CommonResult(444,"查询数据失败，查询id"+id,null);
        }
    };

    @GetMapping(value = "/payment/lb")
    public String getPaymentLB(){
        return serverPort;
    }

    /**
     * 模拟复杂业务，长流程调用
     * @return
     */
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
}
