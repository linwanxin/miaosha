package com.yulenka.miaosha.exception;

import com.yulenka.miaosha.result.CodeMsg;
import com.yulenka.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;

/**
 * @Descripiton:全局异常处理
 * @Author:linwx
 * @Date；Created in 1:13 2019/7/23
 **/
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class )
    public Result<String> exceptionHandler(Exception e){
        e.printStackTrace();
        if(e instanceof GlobalException){
            GlobalException ge = (GlobalException) e;
            return Result.error(ge.getCodeMsg());
        }else if(e instanceof BindException){
            BindException bx = (BindException) e;
            List<ObjectError> errors = bx.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

}
