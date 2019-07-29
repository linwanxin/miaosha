package com.yulenka.miaosha.vo;


import com.yulenka.miaosha.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {
	private GoodsVo goods;
	private OrderInfo order;
}
