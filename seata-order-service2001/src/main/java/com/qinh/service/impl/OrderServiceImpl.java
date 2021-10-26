package com.qinh.service.impl;

import com.qinh.dao.OrderDao;
import com.qinh.domain.Order;
import com.qinh.service.AccountService;
import com.qinh.service.OrderService;
import com.qinh.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-26 20:25
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private StorageService storageService;

    @Autowired
    private AccountService accountService;



    @Override
    public void create(Order order) {
        log.info("开始新建订单......");
        orderDao.create(order);

        log.info("订单微服务调用库存，做扣减....");
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("扣减成功");

        log.info("账户微服务修改账户余额，做扣减.....");
        accountService.decrease(order.getUserId(), order.getMoney());
        log.info("扣减成功");

        //修改订单状态，从0 -> 1，1代表已经完成
        log.info("修改订单状态开始");
        orderDao.update(order.getUserId(), 0);
        log.info("修改完成");

        log.info("下订单全部完成..........");

    }
}
