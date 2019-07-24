package com.yulenka.miaosha.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 19:16 2019/7/22
 **/
@Data
public class MSUser {
    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}
