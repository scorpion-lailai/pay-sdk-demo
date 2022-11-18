package com.baidu.web.service.impl;

import com.baidu.base.utils.pay.NotifyUrl;
import com.baidu.base.utils.pay.PayNotifyParam;
import com.baidu.base.utils.pay.wechat.WeChatPayHelperV3;
import com.baidu.web.service.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
public class WechatPayServiceImpl implements WechatPayService {
    @Override
    public Map<String, String> wechatPayAPP(Long orderId, BigDecimal amount) {
        return WeChatPayHelperV3.APP(orderId, amount, NotifyUrl.WECHAT_PAY_ORDER_URL);
    }

    @Override
    public Map<String, String> wechatPayPC(Long orderId, BigDecimal amount) {
        return WeChatPayHelperV3.PC(orderId, amount, NotifyUrl.WECHAT_PAY_ORDER_URL);
    }

    @Override
    public Map<String, String> wechatPayH5(Long orderId, BigDecimal amount) {
        return WeChatPayHelperV3.H5(orderId, amount, NotifyUrl.WECHAT_PAY_ORDER_URL);
    }

    @Override
    public Map<String, String> wechatPayMiniProgram(Long orderId, String openId, BigDecimal amount) {
        return WeChatPayHelperV3.MiniProgram(
                orderId, amount, NotifyUrl.WECHAT_PAY_ORDER_URL, openId);
    }

    @Override
    public void wechatPayNotify(HttpServletRequest request) {
        PayNotifyParam param = WeChatPayHelperV3.wechatNotify(request);
        log.info("订单ID：{}", param.getOrderId());
        log.info("商户订单号：{}", param.getTradeNo());
        log.info("交易状态：{}", param.getTradeStatus());
        log.info("订单金额：{}", param.getPayMoney());
        log.info("交易时间:{}", param.getPayTime());
        //  后续逻辑自己处理

    }
}
