package com.qinh.service;

import com.qh.springcloud.entities.CommonResult;
import com.qh.springcloud.entities.Payment;
import org.springframework.stereotype.Component;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-24 22:00
 */
@Component
public class PaymentFallbackService implements PaymentService {
    @Override
    public CommonResult<Payment> paymentSQL(Long id)
    {
        return new CommonResult<>(44444,"服务降级返回,---PaymentFallbackService",new Payment(id,"errorSerial"));
    }
}
