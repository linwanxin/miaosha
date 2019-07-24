package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.service.GoodsService;
import com.yulenka.miaosha.service.MSUserService;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 12:50 2019/7/23
 **/
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    public String list(Model model,MSUser msUser){
        model.addAttribute("user", msUser);
        List<GoodsVo> goodsList = goodsService.getAllGoodsVo();
        model.addAttribute("goodsList",goodsList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model, MSUser msUser, @PathVariable("goodsId") long goodsId){
        model.addAttribute("user",msUser);

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        //时间
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();

        int msStatus = 0;//ms状态
        int remainSeconds = 0;//剩余时间
        if(nowTime < startTime){
            msStatus = 0;
            remainSeconds = (int)((startTime-nowTime)/1000);
        }else if(nowTime > endTime){
            msStatus = 2;
            remainSeconds = -1;
        }else{
            msStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", msStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goods_detail";
    }
}
