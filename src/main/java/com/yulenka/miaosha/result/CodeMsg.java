package com.yulenka.miaosha.result;

import lombok.Data;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 1:18 2019/7/21
 **/
@Data
public class CodeMsg {
    private int code;
    private String msg;

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");

    //登录模块 5002XX
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500211,"手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500212,"密码错误");

    //商品模块 5003XX

    //订单模块 5004XX

    //秒杀模块 5005XX
    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
    public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "不能重复秒杀");

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String msg = String.format(this.msg,args);
        return new CodeMsg(code,msg);
    }
}
