package com.yulenka.miaosha.dao;

import com.yulenka.miaosha.domain.MSGoods;
import com.yulenka.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 23:43 2019/7/23
 **/
@Mapper
public interface GoodsDao {

    @Select("select g.*,mg.ms_price,mg.stock_count,mg.start_date,mg.end_date from ms_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> getAllGoodsVo();

    @Select("select g.*,mg.ms_price,mg.stock_count,mg.start_date,mg.end_date from ms_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update ms_goods set stock_count = stock_count -1 where goods_id = #{goodsId}")
    public int reduceStock(MSGoods msGoods);
}
