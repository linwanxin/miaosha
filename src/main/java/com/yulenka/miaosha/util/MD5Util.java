package com.yulenka.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 18:21 2019/7/22
 **/
public class MD5Util {

    //前台的salt是固定的，后台的salt是随机生成的
    public static final String salt = "1a2b3c4d";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }


    public static String inputPassToFormPass(String passwd){  //123456
        String str = ""+salt.charAt(0)+salt.charAt(2) + passwd +salt.charAt(5) + salt.charAt(4);  //12123456c3
        return md5(str);
    }

    public static String formPassToDbPass(String formPass,String salt){
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }


    public static String inputPassToDbPass(String s, String salt) {
        String formPass = inputPassToFormPass(s);
        String dbPass = formPassToDbPass(formPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456"));
        System.out.println(formPassToDbPass(inputPassToFormPass("123456"),salt));
    }

}
