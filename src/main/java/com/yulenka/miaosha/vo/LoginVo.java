package com.yulenka.miaosha.vo;

import com.yulenka.miaosha.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 18:48 2019/7/22
 **/
@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private  String mobile;
    @NotNull
    @Length(min = 32)
    private String password;
}
