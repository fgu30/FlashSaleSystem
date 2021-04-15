package com.sqn.seckill.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title: ValidatorUtil
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/7 0007 下午 6:12
 */
public class ValidatorUtil {

    private static Pattern MOBILE_PATTERN = Pattern.compile("[1]([3-9])[0-9]{9}$");

    /**
     * 手机号码校验
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile){
        if (StringUtils.isEmpty(mobile)){
            return false;
        }
        Matcher matcher = MOBILE_PATTERN.matcher(mobile);
        return matcher.matches();
    }
}
