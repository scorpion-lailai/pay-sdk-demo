package com.baidu.web.service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

public interface WechatPayService {

    Map<String, String> wechatPayAPP(Long orderId, BigDecimal amount);

    Map<String, String> wechatPayPC(Long orderId, BigDecimal amount);

    Map<String, String> wechatPayH5(Long orderId, BigDecimal amount);

    Map<String, String> wechatPayMiniProgram(Long orderId, String openId,BigDecimal amount);

    void wechatPayNotify(HttpServletRequest request);


}
