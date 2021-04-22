package com.sqn.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Title: Md5Util
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2020/4/7 0007 上午 11:50
 */
public class Md5Util {

    /**
     * 盐
     */
    private static final String SALT = "1a2b3c4d";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 第一次加密
     *
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass) {
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    /**
     * 第二次加密
     *
     * @param formPass
     * @param salt
     * @return
     */
    public static String formPassToDbPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 两次MD5加密
     *
     * @param inputPass
     * @param salt
     * @return
     */
    public static String inputPassToDbPass(String inputPass, String salt) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDbPass(formPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        //d3b1294a61a07da9b49b6e22b2cbd7f9
        //b7797cce01b4b131b433b6acf4add449
        System.out.println(inputPassToFormPass("123456"));
        System.out.println(formPassToDbPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
        System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));
    }

}
