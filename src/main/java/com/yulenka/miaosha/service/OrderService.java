package com.yulenka.miaosha.service;

import com.yulenka.miaosha.dao.OrderDao;
import com.yulenka.miaosha.domain.MSOrder;
import com.yulenka.miaosha.domain.MSUser;
import com.yulenka.miaosha.domain.OrderInfo;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 15:02 2019/7/24
 **/
@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public MSOrder getMSOrderByUserIdGoodsId(Long userId, long goodsId) {
        return orderDao.getMSOrderByUserIdGoodsId(userId,goodsId);
    }

    public OrderInfo createOrder(MSUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        long orderId = orderDao.insert(orderInfo);
        MSOrder msOrder = new MSOrder();
        msOrder.setGoodsId(goods.getId());
        msOrder.setOrderId(orderId);
        msOrder.setUserId(user.getId());
        orderDao.insertMSOrder(msOrder);
        return orderInfo;
    }

}
