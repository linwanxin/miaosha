package com.yulenka.miaosha.redis;

/**
 * Author:林万新 lwx
 * Date:  2019/7/30
 * Time: 20:48
 */
public class MSKey extends BasePrefix {

    public MSKey(int expiredSeconds, String prefix) {
        super(expiredSeconds, prefix);
    }

    public static MSKey isGoodsOver = new MSKey(0,"go");
    public static MSKey getMisoshaPath = new MSKey(60,"mp");
    public static MSKey getMiaoshaVeriyCode = new MSKey(300,"vc");

}
