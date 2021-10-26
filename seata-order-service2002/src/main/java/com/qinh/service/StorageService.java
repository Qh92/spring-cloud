package com.qinh.service;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-26 23:12
 */
public interface StorageService {
    // 扣减库存
    void decrease(Long productId, Integer count);
}
