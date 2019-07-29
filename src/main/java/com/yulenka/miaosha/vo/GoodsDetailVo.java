package com.yulenka.miaosha.vo;

import com.yulenka.miaosha.domain.MSUser;
import lombok.Data;

/**
 * Author:林万新 lwx
 * Date:  2019/7/28
 * Time: 18:35
 */
@Data
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods ;
    private MSUser user;

}
