package com.qinh.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.qh.springcloud.entities.CommonResult;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-23 21:15
 */
public class CustomerBlockHandler {

    public static CommonResult handleException2(BlockException exception) {
        return new CommonResult(2020, "自定义限流处理信息....CustomerBlockHandler");

    }
}
