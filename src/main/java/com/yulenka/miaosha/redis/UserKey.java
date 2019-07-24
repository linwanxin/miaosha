package com.yulenka.miaosha.redis;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 11:54 2019/7/22
 **/
public class UserKey extends BasePrefix {

    private UserKey(String prefix) {
        super(prefix);
    }

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private UserKey(int expireSeconds,String prefix){
        super(expireSeconds,prefix);
    }
    public static UserKey token = new UserKey(TOKEN_EXPIRE,"tk");

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");

}
