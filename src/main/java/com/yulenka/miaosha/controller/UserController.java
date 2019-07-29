package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.result.Result;
import com.yulenka.miaosha.service.GoodsService;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 12:50 2019/7/23
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MSUser> info(Model model, MSUser msUser){
        model.addAttribute("user", msUser);
        return Result.success(msUser);
    }


}
