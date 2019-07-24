package com.yulenka.miaosha.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Descripiton:正则校验手机号
 * @Author:linwx
 * @Date；Created in 0:25 2019/7/23
 **/
public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");


    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return false;
        }
        Matcher matcher =  mobile_pattern.matcher(src);
        return matcher.matches();
    }

//    	public static void main(String[] args) {
//			System.out.println(isMobile("18912341234"));
//			System.out.println(isMobile("1891234123"));
//	}
}
