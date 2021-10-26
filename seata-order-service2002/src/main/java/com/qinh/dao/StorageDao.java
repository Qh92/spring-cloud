package com.qinh.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-26 23:10
 */
@Mapper
public interface StorageDao {

    //扣减库存信息
    void decrease(@Param("productId") Long productId, @Param("count") Integer count);
}
