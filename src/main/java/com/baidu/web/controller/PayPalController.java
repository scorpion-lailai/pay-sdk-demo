package com.baidu.web.controller;

import com.baidu.base.json.Result;
import com.baidu.web.service.PayPalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RestController
@RequestMapping("paypal")
@Api(tags = "PayPal支付")
public class PayPalController {

    @Resource
    private PayPalService paypalService;

    public static final String SUB_PAY_NOTES =
            "<font color = 'red'>" +
                    "订阅支付要先创建商品哦" +
                    "</font>";

    @ApiOperation(value = "paypal订单支付")
    @PostMapping("order/pay")
    public String paypalOrderPay(Long orderId, BigDecimal orderMoney) {
        Object payLink = paypalService.paypalOrderPay(orderMoney, orderId);
        return Result.ok(payLink);
    }

    @ApiOperation(value = "paypal订阅支付", notes = SUB_PAY_NOTES)
    @PostMapping("subscription/pay")
    public String paypalOrderPay(String planId, Long orderId) {
        Object payLink = paypalService.createSubscription(planId, orderId);
        return Result.ok(payLink);
    }

    @PostMapping("subscription/notify")
    @ApiOperation(value = "订阅支付回调")
    public String paypalSubscriptionNotify(String subscriptionId) {
        paypalService.subscriptionNotify(subscriptionId);
        return Result.ok();
    }

    @PostMapping("order/notify")
    @ApiOperation(value = "订单支付回调")
    public String paypalNotify(String paymentId, String PayerId) {
        paypalService.paypalNotify(paymentId, PayerId);
        return Result.ok();
    }

}
