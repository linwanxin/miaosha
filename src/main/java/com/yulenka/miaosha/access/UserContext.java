package com.yulenka.miaosha.access;

import com.yulenka.miaosha.domain.MSUser;

/**
 * Author:林万新 lwx
 * Date:  2019/8/2
 * Time: 12:00
 */
public class UserContext {
    private static ThreadLocal<MSUser>  userHolder = new ThreadLocal<>();

    public static void set (MSUser msUser){
        userHolder.set(msUser);
    }
    public static MSUser get (){
        return userHolder.get();
    }

}
