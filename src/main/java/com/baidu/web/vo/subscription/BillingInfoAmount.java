package com.baidu.web.vo.subscription;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillingInfoAmount {
    private String currency_code;
    private BigDecimal value;
}
