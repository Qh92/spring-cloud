package com.qh.springcloud.service.impl;

import com.qh.springcloud.dao.PaymentDao;
import com.qh.springcloud.entities.Payment;
import com.qh.springcloud.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Qh
 * @Date 2020/9/20 20:31
 * @Version 1.0
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    @Resource
    private PaymentDao paymentDao;

    public int create(Payment payment){
        return paymentDao.create(payment);
    };
    public Payment getPaymentById(Long id){
        return paymentDao.getPaymentById(id);
    };

}
