package com.yulenka.miaosha.controller;

import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.OrderInfo;
import com.yulenka.miaosha.result.CodeMsg;
import com.yulenka.miaosha.result.Result;
import com.yulenka.miaosha.service.GoodsService;
import com.yulenka.miaosha.service.OrderService;
import com.yulenka.miaosha.vo.GoodsVo;
import com.yulenka.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author:林万新 lwx
 * Date:  2019/7/29
 * Time: 6:22
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    @GetMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> detail(MSUser msUser, @RequestParam("orderId")long orderId){
        if(msUser == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goods);
        orderDetailVo.setOrder(order);
        return Result.success(orderDetailVo);
    }


}
