package com.yulenka.miaosha.vo;

import com.yulenka.miaosha.domain.Goods;
import lombok.Data;

import java.util.Date;

/**
 * @Descripiton:
 * @Author:linwx
 * @Date；Created in 23:44 2019/7/23
 **/
@Data
public class GoodsVo extends Goods {
    private Double msPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
