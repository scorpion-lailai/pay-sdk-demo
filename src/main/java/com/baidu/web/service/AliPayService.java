package com.baidu.web.service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface AliPayService {

    // 支付宝支付PC端
    String alipayPC(Long orderId, String returnUrl, BigDecimal amount);

    String alipayH5(Long orderId, String returnUrl, BigDecimal amount);

    String alipayAPP(Long orderId, BigDecimal amount);

    // 支付宝支付回调
    void alipayNotify(HttpServletRequest request);
}
