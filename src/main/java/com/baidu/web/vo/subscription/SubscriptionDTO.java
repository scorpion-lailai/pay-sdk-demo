/**
  * Copyright 2021 json.cn 
  */
package com.baidu.web.vo.subscription;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * Auto-generated: 2021-10-11 20:5:48
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */

@Data
public class SubscriptionDTO{
    /**
     * 计划ID required
     */
    @JsonProperty("plan_id")
    private String planId;

    /**
     * 开始时间
     */
    @JsonProperty(value = "start_time")
    private Date startTime;

    /**
     * 数量 订阅产品的数量。
     * 最小值 1 最大值32
     */
    private String quantity;

    /**
     * 运费
     */
    @JsonProperty("shipping_amount")
    private ShippingAmount shippingAmount;

    /**
     * 订阅者请求信息。
     */
    private Subscriber subscriber;

    /**
     * 应用程序上下文
     */
    @JsonProperty("application_context")
    private ApplicationContext applicationContext;

    /**
     *原样返回域
     */
    @JsonProperty("custom_id")
    private String customId;


}