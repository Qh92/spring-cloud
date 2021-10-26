package com.qinh.controller;

import com.qinh.domain.CommonResult;
import com.qinh.domain.Order;
import com.qinh.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-26 20:41
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/order/create")
    public CommonResult create(Order order)
    {
        orderService.create(order);
        return new CommonResult(200,"订单创建成功");
    }

}
