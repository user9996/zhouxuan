package com.springboot.sample.bean;


import lombok.Data;

@Data
public class User {

    private Long id;

    // 用户名
    private String name;

    // 积分 查积分系统
    private Integer integral;

    // 优惠劵 查优惠劵系统
    private Integer coupon;

    // requestId
    private String requestId;


}
