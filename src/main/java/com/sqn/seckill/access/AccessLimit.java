package com.sqn.seckill.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Title: AccessLimit
 * Description: 接口访问限制注解
 * 访问限制：@AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/30 0030 上午 10:13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    int seconds();

    int maxCount();

    boolean needLogin() default true;
}
