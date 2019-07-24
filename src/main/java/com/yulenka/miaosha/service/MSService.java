package com.yulenka.miaosha.service;

import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.OrderInfo;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 15:25 2019/7/24
 **/
@Service
public class MSService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MSUser user, GoodsVo goods){
        //减库存
        goodsService.reduceStock(goods);
        //生成秒杀订单
        return orderService.createOrder(user,goods);
    }

}
