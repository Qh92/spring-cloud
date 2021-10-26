package com.qinh.dao;

import com.qinh.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-26 20:11
 */
@Mapper
public interface OrderDao {

    void create(Order order);

    void update(@Param("userId") Long userId,@Param("status") Integer status);
}
