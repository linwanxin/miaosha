package com.yulenka.miaosha.domain;

import lombok.Data;

@Data
public class MSOrder {
	private Long id;
	private Long userId;
	private Long  orderId;
	private Long goodsId;
}
