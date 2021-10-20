package com.qinh.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-17 16:09
 */
@RestController
@Slf4j
public class FlowLimitController {
    @GetMapping("/testA")
    public String testA() {
        //模拟操作流程
        /*try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        System.out.println(Thread.currentThread().getName() + " : testA");
        return "------testA";
    }

    @GetMapping("/testB")
    public String testB() {

        return "------testB";
    }

    @GetMapping("/testD")
    public String testD()
    {
        /*try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        log.info("testD 测试RT");*/

        log.info("testD 测试RT");
        int age = 10/0;

        return "------testD";
    }

    @GetMapping("/testE")
    public String testE()
    {
        log.info("testE 测试异常数");
        int age = 10/0;
        return "------testE 测试异常数";
    }

    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler = "dealTestHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false) String p1,
                             @RequestParam(value = "p2",required = false) String p2){
        return "---testHotKey";
    }

    public String dealTestHotKey(String p1, String p2, BlockException exception){
        return "---dealTestHotKey,kuku.......";
    }


}
