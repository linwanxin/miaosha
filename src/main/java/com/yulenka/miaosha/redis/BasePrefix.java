package com.yulenka.miaosha.redis;

import com.fasterxml.jackson.databind.ser.Serializers;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 11:29 2019/7/22
 **/
public class BasePrefix implements KeyPrefix {

    private int expiredSeconds;
    private String prefix;

    //抽象类不能被实例化，所以可用public
    public BasePrefix(int expiredSeconds,String prefix){
        this.expiredSeconds = expiredSeconds;
        this.prefix = prefix;
    }
    //0代表永不过期！
    public BasePrefix(String prefix){
        //new来调用，或者this！
        //new BasePrefix(0,prefix);
        this(0,prefix);
    }


    @Override
    public int getExpireSeconds() {
        return expiredSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
