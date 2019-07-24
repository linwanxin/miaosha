package com.yulenka.miaosha.redis;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 11:20 2019/7/22
 **/
public interface KeyPrefix {


    public int getExpireSeconds();

    public String getPrefix();

}
