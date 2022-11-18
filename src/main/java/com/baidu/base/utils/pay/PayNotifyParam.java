package com.baidu.base.utils.pay;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayNotifyParam {
  // 订单id
  private Long orderId;
  // 商户订单号
  private String tradeNo;
  // 交易状态 1.成功 0.失败
  private Integer tradeStatus;
  public static final int TRADE_STATUS_SUCCESS = 1;
  // 交易金额
  private BigDecimal payMoney;
  // 交易时间
  private String payTime;
}
