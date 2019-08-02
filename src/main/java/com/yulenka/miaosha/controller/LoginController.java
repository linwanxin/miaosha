package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.result.Result;
import com.yulenka.miaosha.service.MSUserService;
import com.yulenka.miaosha.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 18:37 2019/7/22
 **/
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    MSUserService userService;

    @GetMapping("/")
    public String login(){
        return "login";
    }

    @PostMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        log.info(loginVo.toString());
        //登陆
        String token = userService.login(response,loginVo);
        return Result.success(true);
    }

}
