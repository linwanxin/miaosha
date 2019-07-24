package com.yulenka.miaosha.exception;

import com.yulenka.miaosha.result.CodeMsg;
import lombok.Data;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 11:58 2019/7/23
 **/
@Data
public class GlobalException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.toString());//why?
        this.codeMsg = codeMsg;
    }



}
