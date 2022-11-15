/**
  * Copyright 2021 json.cn 
  */
package com.baidu.web.vo.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 订阅者请求信息
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class Subscriber {
    /**
     * 姓名
     */
    @JsonProperty("name")
    private SubscriberName name;

    /**
     * 邮件地址
     */
    @JsonProperty("email_address")
    private String emailAddress;

    /**
     * 发货地址
     */
    @JsonProperty("shipping_address")
    private ShippingAddress shippingAddress;


}