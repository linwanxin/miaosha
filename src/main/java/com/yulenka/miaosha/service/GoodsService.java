package com.yulenka.miaosha.service;

import com.yulenka.miaosha.dao.GoodsDao;
import com.yulenka.miaosha.domain.MSGoods;
import com.yulenka.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 23:42 2019/7/23
 **/
@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> getAllGoodsVo(){
        return  goodsDao.getAllGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId){
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        MSGoods msGoods = new MSGoods();
        msGoods.setGoodsId(goods.getId());
        int ret = goodsDao.reduceStock(msGoods);
        return ret > 0;
    }
}
