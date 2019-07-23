package com.yulenka.miaosha.result;

import lombok.Data;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 20:45 2019/7/20
 **/
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;


    private Result(T data){
        this.code  = 0;
        this.msg = "success";
        this.data = data;
    }


    /**
     * 成功调用
      * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data){
        return new Result<>(data);
    }


    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<>(codeMsg);
    }


    private Result(CodeMsg cm) {
        if(cm == null) {
            return;
        }
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }
}
