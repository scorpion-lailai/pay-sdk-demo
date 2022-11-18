package com.baidu.base.utils.pay;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum NotifyUrl {

    // 支付回调重定向URL
    ALIPAY_ORDER_URL("alipay/notify"),
    WECHAT_PAY_ORDER_URL("wechatPay/notify"),
    ;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
