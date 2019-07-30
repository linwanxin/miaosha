package com.yulenka.miaosha.service;

import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.OrderInfo;
import com.yulenka.miaosha.redis.MSKey;
import com.yulenka.miaosha.redis.RedisService;
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
    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MSUser user, GoodsVo goods){
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if(success){
            //生成秒杀订单
            return orderService.createOrder(user,goods);
        }else{
            setGoodsOver(goods.getId());
            return null;
        }
    }




    public long getResult(long userId, long goodsId) {
        MSOrder order = orderService.getMSOrderByUserIdGoodsId(userId,goodsId);
        if(order != null){
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else{
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MSKey.isGoodsOver,""+goodsId);
    }

    public void setGoodsOver(Long goodsId) {
        redisService.set(MSKey.isGoodsOver,""+goodsId,true);
    }
}
