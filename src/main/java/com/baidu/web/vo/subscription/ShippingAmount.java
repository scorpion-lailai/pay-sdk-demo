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
public class ShippingAmount{
    /**
     * 货币代码
     */
    @JsonProperty("currency_code")
    private String currencyCode;

    /**
     * 金额
     */
    private String value;


}