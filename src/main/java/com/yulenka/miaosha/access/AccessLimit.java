package com.yulenka.miaosha.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author:林万新 lwx
 * Date:  2019/8/2
 * Time: 11:51
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    int maxCount();
    int seconds();
    boolean needLogin() default true;
 }
