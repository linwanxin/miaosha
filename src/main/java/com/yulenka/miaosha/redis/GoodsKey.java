package com.yulenka.miaosha.redis;

/**
 * Author:林万新 lwx
 * Date:  2019/7/28
 * Time: 16:54
 */
public class GoodsKey extends BasePrefix {
    public GoodsKey(int expiredSeconds, String prefix) {
        super(expiredSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
    public static GoodsKey getMSGoodsStock = new GoodsKey(0,"gs");
}
