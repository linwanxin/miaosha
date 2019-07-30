package com.yulenka.miaosha.rabbitmq;

import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.redis.RedisService;
import com.yulenka.miaosha.service.GoodsService;
import com.yulenka.miaosha.service.MSService;
import com.yulenka.miaosha.service.OrderService;
import com.yulenka.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author:林万新 lwx
 * Date:  2019/7/30
 * Time: 0:33
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MSService msService;
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        log.info("receive message:" + message);
        MSMessage mm = redisService.strToBean(message,MSMessage.class);
        MSUser msUser = mm.getMsUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getGoodsStock();
        if(stock <= 0){
            return;
        }
        MSOrder order = orderService.getMSOrderByUserIdGoodsId(msUser.getId(),goodsId);
        if(order != null){
            return;
        }
        //生成秒杀订单
        msService.miaosha(msUser,goodsVo);
    }

    /**
     * Direct 模式
     * @param message
     */
    /**
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        log.info("receive message:" + message);

    }
    */
}
