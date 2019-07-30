package com.yulenka.miaosha.redis;

/**
 * Author:林万新 lwx
 * Date:  2019/7/30
 * Time: 20:48
 */
public class MSKey extends BasePrefix {


    public MSKey(String prefix) {
        super(prefix);
    }
    public static MSKey isGoodsOver = new MSKey("go");

}
