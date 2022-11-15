package com.baidu.web.vo.subscription;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BillingInfo {

    private LastPayment last_payment;

    private List<CycleExecutions> cycle_executions;
    //下个计费时间
    private Date next_billing_time;
}
