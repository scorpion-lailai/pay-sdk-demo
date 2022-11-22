package com.baidu.web.service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

public interface WechatPayService {
    //APP支付
    Map<String, String> wechatPayAPP(Long orderId, BigDecimal amount);

    //PC支付
    Map<String, String> wechatPayPC(Long orderId, BigDecimal amount);

    //H5支付
    Map<String, String> wechatPayH5(Long orderId, BigDecimal amount);

    //小程序支付
    Map<String, String> wechatPayMiniProgram(Long orderId, String openId, BigDecimal amount);

    //支付回调
    void wechatPayNotify(HttpServletRequest request);


}
