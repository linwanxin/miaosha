package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.domain.User;
import com.yulenka.miaosha.redis.KeyPrefix;
import com.yulenka.miaosha.redis.RedisService;
import com.yulenka.miaosha.redis.UserKey;
import com.yulenka.miaosha.result.CodeMsg;
import com.yulenka.miaosha.result.Result;
import com.yulenka.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 23:18 2019/7/19
 **/
@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @GetMapping("/hello")
    public String thymeleaf(Model model){
        model.addAttribute("name","linwx");
        return "hello";
    }

    @GetMapping("/")
    @ResponseBody
    public String home(){
        return "home";
    }

    @GetMapping("/success")
    @ResponseBody
    public Result success(){
        return Result.success(CodeMsg.SUCCESS);
    }

    @GetMapping("/error")
    @ResponseBody
    public Result error(){
        return Result.error(CodeMsg.SERVER_ERROR);
        //return new Result(500102, "XXX");
    }


    /**
     * 测试mybatis集成
     */
    @GetMapping("db/get")
    @ResponseBody
    public Result<User> getUser(){
        User user = userService.getUserById(1);
        return Result.success(user);
    }

    /**
     * redis集成
     * @return
     */
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> setRedis(){
        User user = new User();
        user.setId(1);
        user.setName("linwx");
        boolean b = redisService.set(UserKey.getById,""+1,user);
        return  Result.success(b);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> getRedisUser(){
        User user  = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }



}
