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
public class PaymentMethod {
    /**
     * 在商家网站上客户选择的付款方式。
     */
    @JsonProperty("payer_selected")
    private String payerSelected;

    /**
     * 收款人 _ 首选
     * 商家-首选的支付方式。
     * 枚举：
     * UNRESTRICTED                 接受来自客户的任何类型的付款
     * IMMEDIATE_PAYMENT_REQUIRED   只接受客户的即时付款。例如，信用卡、 PayPal 余额或即时 ACH。确保在捕获时，付款没有“挂起”状态
     */
    @JsonProperty("payee_preferred")
    private String payeePreferred;

}