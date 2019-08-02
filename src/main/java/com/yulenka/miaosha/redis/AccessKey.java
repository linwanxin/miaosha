package com.yulenka.miaosha.redis;

/**
 * Author:林万新 lwx
 * Date:  2019/8/2
 * Time: 11:04
 */
public class AccessKey extends BasePrefix {

    public AccessKey(int expiredSeconds, String prefix) {
        super(expiredSeconds, prefix);
    }
    //5S有效期 ：配合注解使用
    //public static AccessKey access = new AccessKey(5,"ak");

    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds,"access");
    }

}
