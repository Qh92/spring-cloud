package com.qinh.domain;

import lombok.Data;

/**
 * @author Qh
 * @version 1.0
 * @date 2021-10-26 23:09
 */
@Data
public class Storage {

    private Long id;

    // 产品id
    private Long productId;

    //总库存
    private Integer total;


    //已用库存
    private Integer used;


    //剩余库存
    private Integer residue;
}
