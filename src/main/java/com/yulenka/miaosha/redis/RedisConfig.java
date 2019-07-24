package com.yulenka.miaosha.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 0:41 2019/7/22
 **/
@Component
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisConfig {

    private String host;
    private int port;
    private int timeout;//秒
    private String password;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;//秒
}
