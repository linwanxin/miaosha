package com.yulenka.miaosha.domain;

import lombok.Data;

@Data
public class MSOrder {
	private long id;
	private Long userId;
	private long  orderId;
	private Long goodsId;
}
