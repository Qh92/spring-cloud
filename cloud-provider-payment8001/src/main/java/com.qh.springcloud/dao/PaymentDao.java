package com.qh.springcloud.dao;

import com.qh.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Qh
 * @Date 2020/9/20 20:00
 * @Version 1.0
 */
@Mapper
public interface PaymentDao {
    int create(Payment payment);
    Payment getPaymentById(@Param("id") Long param_id);
    /**
     * 1，使用@Param注解
     * 当以下面的方式进行写SQL语句时：
     *
     *     @Select("select column from table where userid = #{userid} ")
     *     public int selectColumn(int userid);
     *
     * 当你使用了使用@Param注解来声明参数时，如果使用 #{} 或 ${} 的方式都可以。
     *     @Select("select column from table where userid = ${userid} ")
     *     public int selectColumn(@Param("userid") int userid);
     *
     * 当你不使用@Param注解来声明参数时，必须使用使用 #{}方式。如果使用 ${} 的方式，会报错。
     *     @Select("select column from table where userid = ${userid} ")
     *     public int selectColumn(@Param("userid") int userid);
     *
     *
     * 2，不使用@Param注解
     * 不使用@Param注解时，参数只能有一个，并且是Javabean。在SQL语句里可以引用JavaBean的属性，而且只能引用JavaBean的属性。
     *
     *     // 这里id是user的属性
     *     @Select("SELECT * from Table where id = ${id}")
     *     Enchashment selectUserById(User user);
     */
}
