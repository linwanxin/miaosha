package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.OrderInfo;
import com.yulenka.miaosha.result.CodeMsg;
import com.yulenka.miaosha.result.Result;
import com.yulenka.miaosha.service.GoodsService;
import com.yulenka.miaosha.service.MSService;
import com.yulenka.miaosha.service.OrderService;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 14:24 2019/7/24
 **/
@Controller
@RequestMapping("/miaosha")
public class MSController {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MSService msService;

    @PostMapping("/do_miaosha2")
    public String miaosha2(Model model, MSUser user,
                          @RequestParam("goodsId") long goodsId){
        model.addAttribute("user",user);

        if(user == null){
            return "login";
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        //先判断库存
        int stock = goods.getStockCount();
        if(stock <= 0){
            model.addAttribute("errmsg",CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //是否已经秒杀过
        MSOrder msOrder =  orderService.getMSOrderByUserIdGoodsId(user.getId(),goodsId);
        if(msOrder != null){
            model.addAttribute("errmsg",CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //进行秒杀业务逻辑
        OrderInfo orderInfo = msService.miaosha(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
        
    }
    @PostMapping("/do_miaosha")
    @ResponseBody
    //确实+这里就没问题了，估计是数据库问题！
    public  Result<OrderInfo> miaosha(MSUser user,
                                     @RequestParam("goodsId") long goodsId){

        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //先判断库存 :+上syn会出现11件！！
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //是否已经秒杀过
        MSOrder msOrder =  orderService.getMSOrderByUserIdGoodsId(user.getId(),goodsId);
        if(msOrder != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //进行秒杀业务逻辑
        OrderInfo orderInfo = msService.miaosha(user,goods);
        return Result.success(orderInfo);

    }

}
