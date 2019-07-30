package com.yulenka.miaosha.rabbitmq;

import com.yulenka.miaosha.domain.MSUser;
import lombok.Data;

/**
 * Author:林万新 lwx
 * Date:  2019/7/30
 * Time: 15:15
 */
@Data
public class MSMessage {
    private MSUser msUser;
    private long goodsId;
}
