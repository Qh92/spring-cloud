package com.qh.springcloud.service;

import com.qh.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Qh
 * @Date 2020/9/20 20:30
 * @Version 1.0
 */
public interface PaymentService {
    public int create(Payment payment);
    public Payment getPaymentById(@Param("id") Long id);
}
