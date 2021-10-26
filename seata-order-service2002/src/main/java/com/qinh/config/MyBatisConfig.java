package com.qinh.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-26 20:43
 */
@Configuration
@MapperScan({"com.qinh.dao"})
public class MyBatisConfig {

}
