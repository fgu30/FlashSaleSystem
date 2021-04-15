package com.sqn.seckill.utils;

import java.util.UUID;

/**
 * Title: UUIDUtil
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2020/4/14 0014 上午 9:41
 */
public class UUIDUtil {

    /**
     * 去掉UUID的“-”
     * @return
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
