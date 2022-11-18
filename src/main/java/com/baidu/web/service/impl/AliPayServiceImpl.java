package com.baidu.web.service.impl;

import com.baidu.base.utils.pay.alipay.AliPayHelper;
import com.baidu.base.utils.pay.NotifyUrl;
import com.baidu.base.utils.pay.PayNotifyParam;
import com.baidu.web.service.AliPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Service("alipayService")
@Slf4j
public class AliPayServiceImpl implements AliPayService {

    @Override
    public String alipayPC(Long orderId, String returnUrl, BigDecimal amount) {
        return AliPayHelper.PC(orderId, amount, returnUrl, NotifyUrl.ALIPAY_ORDER_URL);
    }

    @Override
    public String alipayH5(Long orderId, String returnUrl, BigDecimal amount) {
        return AliPayHelper.H5(orderId, amount, returnUrl, NotifyUrl.ALIPAY_ORDER_URL);
    }

    @Override
    public String alipayAPP(Long orderId, BigDecimal amount) {
        return AliPayHelper.APP(orderId, amount, NotifyUrl.ALIPAY_ORDER_URL);
    }

    @Override
    public void alipayNotify(HttpServletRequest request) {
        PayNotifyParam param = AliPayHelper.notifyParamParse(request);
        log.info("订单ID：{}", param.getOrderId());
        log.info("商户订单号：{}", param.getTradeNo());
        log.info("交易状态：{}", param.getTradeStatus());
        log.info("订单金额：{}", param.getPayMoney());
        log.info("交易时间:{}", param.getPayTime());
        //        后续逻辑自己处理

    }
}
