/**
  * Copyright 2021 json.cn 
  */
package com.baidu.web.vo.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Auto-generated: 2021-10-11 20:5:48
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class ApplicationContext{
    /**
     * 品牌名称
     * 这个标签覆盖了贝宝网站上贝宝账户中的商业名称。
     */
    @JsonProperty("brand_name")
    private String brandName;

    /**
     * 现场
     * 贝宝支付体验显示的 BCP 47格式的页面区域设置。PayPal 支持五个字符的代码。例如，da-DK、 he-IL、 id-ID、 ja-JP、 no-NO、 pt-BR、 ru-RU、 sv-SE、 th-TH、 zh-CN、 zh-HK 或 zh-TW。
     */
    private String locale;

    /**
     * 航运偏好
     * 派生送货地址的位置
     * 枚举：
     * GET_FROM_FILE.           在 PayPal 网站上获得客户提供的送货地址
     * NO_SHIPPING.             从贝宝网站编辑送货地址。推荐用于数码产品
     * SET_PROVIDED_ADDRESS.    获取商家提供的地址。客户不能在 PayPal 网站上更改这个地址。如果商家没有通过一个地址，客户可以选择贝宝网页上的地址
     */
    @JsonProperty("shipping_preference")
    private String shippingPreference;

    /**
     * 用户操作
     * 将标签名称配置为 Continue 或 Subscribe Now，以获得订阅同意体验。
     * CONTINUE.         在将客户重定向到 PayPal 订阅同意页面后,Continue 继续 按钮出现。当您希望控制订阅的激活并且不希望 PayPal 激活订阅时，请使用此选项
     * SUBSCRIBE_NOW      在将客户重定向到 PayPal 订阅同意页面后,Subscribe Now 现在就订阅按钮出现。当您希望贝宝激活订阅时使用此选项
     */
    @JsonProperty(value = "user_action")
    private String userAction;

    /**
     * 付款方法
     */
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    /**
     * 返回 url required
     * 客户批准付款后重定向客户的 URL。
     * 最大长度4000
     */
    @JsonProperty("return_url")
    private String returnUrl;

    /**
     * 取消 url required
     * 客户取消付款后重定向客户的 URL。
     */
    @JsonProperty("cancel_url")
    private String cancelUrl;


}