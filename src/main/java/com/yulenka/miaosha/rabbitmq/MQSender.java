package com.yulenka.miaosha.rabbitmq;

import com.yulenka.miaosha.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author:林万新 lwx
 * Date:  2019/7/30
 * Time: 0:33
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    RedisService redisService;

    @Autowired
    AmqpTemplate amqpTemplate;


    public void sendMSMessage(MSMessage message){
        String msg =  redisService.beanToStr(message);
        log.info("send message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }


    /**
     *
     * @param message
     */
//    public void sender(Object message){
//        String msg = redisService.beanToStr(message);
//        log.info("send message:" + msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
//    }

}
