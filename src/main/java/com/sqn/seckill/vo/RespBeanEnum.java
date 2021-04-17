package com.sqn.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Title: RespBeanEnum
 * Description: 公共返回对象枚举
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/7 0007 下午 5:24
 */
@ToString
@AllArgsConstructor
@Getter
public enum RespBeanEnum {

    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务器异常"),
    //登录模块 5002xx
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    //秒杀模块5005xx
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501,"该商品每人限购一件")
    ;

    private final Integer code;
    private final String message;
}
