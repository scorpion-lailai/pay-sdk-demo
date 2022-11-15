package com.baidu.web.service;

import com.baidu.web.vo.subscription.DetailsResponse;

import java.math.BigDecimal;

public interface PayPalService {


    //获取paypalToken
    String getPaypalToken();

    //创建订阅计划
    Object createSubscription(String planId, Long orderId);

    //订单支付
    Object paypalOrderPay(BigDecimal subtotal, Long orderId);

    //订阅详情
    DetailsResponse getSubscriptionsDetail(String id);

    //订单支付回调
    void paypalNotify(String paymentId, String payerId);

    //订阅支付回调
    void subscriptionNotify(String subscriptionId);
}
