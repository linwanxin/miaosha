package com.yulenka.miaosha.redis;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Descripiton:封装redis服务
 * @Author:linwx
 * @Date；Created in 0:50 2019/7/22
 **/
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * get对象
     * @param keyPrefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realkey = keyPrefix.getPrefix() + key;
            String value = jedis.get(realkey);
            return strToBean(value,clazz);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * set对象
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    //泛型方法:set需要判断下是否设置过期时间
   public <T> boolean set(KeyPrefix keyPrefix,String key,T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = beanToStr(value);
            if(str == null || str.length() <=0){
                return false;
            }
            String realKey = keyPrefix.getPrefix() + key;
            int seconds = keyPrefix.getExpireSeconds();
            if(seconds <= 0){
                jedis.set(realKey,str);
            }else {
                jedis.setex(realKey,seconds,str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
   }

    /**
     * 判断key是否存在
     * @param keyPrefix
     * @param key
     * @return
     */
   public  boolean exists(KeyPrefix keyPrefix,String key){
       Jedis jedis = null;
       try{
           jedis = jedisPool.getResource();
           String realKey = keyPrefix.getPrefix() + key;
           return jedis.exists(realKey);
       }finally {
           returnToPool(jedis);
       }
   }

    /**
     * 增加值
     * @param keyPrefix
     * @param key
     * @return
     */
   public Long incr(KeyPrefix keyPrefix ,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
   }

    /**
     * 减少值
     * @param keyPrefix
     * @param key
     * @return
     */
    public Long decr(KeyPrefix keyPrefix ,String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

   private <T> String beanToStr(T value){
      if(value == null){
          return null;
      }
      Class clazz = value.getClass();
      if(clazz ==int.class|| clazz == Integer.class){
          return "" + value;
      }else if(clazz == String.class){
          return (String) value;
      }else if(clazz == long.class|| clazz == Long.class){
          return ""+ value;
      }else {
          return JSONObject.toJSONString(value);
      }
   }

    private <T> T strToBean(String value, Class<T> clazz){
        if(value == null || value.length() <=0 || clazz == null){
            return null;
        }
        if(clazz == int.class|| clazz == Integer.class){
            return (T) Integer.valueOf(value);
        }else if(clazz == String.class){
            return (T) value;
        }else if(clazz == long.class|| clazz == Long.class){
            return (T) Long.valueOf(value);
        }else {
            return JSONObject.toJavaObject(JSONObject.parseObject(value),clazz);
        }
    }

    private void returnToPool(Jedis jedis){
        if(jedis != null){
            jedis.close();
        }
    }


    public boolean delete(UserKey prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            long ret =  jedis.del(key);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }
}
