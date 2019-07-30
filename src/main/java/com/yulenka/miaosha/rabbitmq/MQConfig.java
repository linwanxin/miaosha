package com.yulenka.miaosha.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author:林万新 lwx
 * Date:  2019/7/30
 * Time: 0:30
 */
@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";
    public static final String MIAOSHA_QUEUE = "MSQueue";


    @Bean
    public Queue queue(){
        return new Queue(MIAOSHA_QUEUE,true);
    }


}
