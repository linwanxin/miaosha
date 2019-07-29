package com.yulenka.miaosha.dao;

import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 15:14 2019/7/24
 **/
@Mapper
public interface OrderDao {

    @Select("select * from ms_order where user_id = #{userId} and goods_id = #{goodsId}")
     MSOrder getMSOrderByUserIdGoodsId(@Param("userId") long userId,@Param("goodsId") long goodsId);


    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values(" +
            "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("insert into ms_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
     int insertMSOrder(MSOrder msOrder);

    @Select("select * from order_info where id = #{orderId}")
     OrderInfo getOrderById(@Param("orderId")long orderId);


}
