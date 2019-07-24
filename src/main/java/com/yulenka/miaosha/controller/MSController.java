package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.OrderInfo;
import com.yulenka.miaosha.result.CodeMsg;
import com.yulenka.miaosha.service.GoodsService;
import com.yulenka.miaosha.service.MSService;
import com.yulenka.miaosha.service.OrderService;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping("/do_miaosha")
    public String miaosha(Model model, MSUser user,
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

}
