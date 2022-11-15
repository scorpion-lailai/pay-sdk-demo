package com.baidu.web.vo.subscription;

import lombok.Data;

@Data
public class DetailsResponse {

    /**
     * APPROVAL_PENDING. 订阅已创建，但尚未得到买家的批准。
     * APPROVED. 买方已批准订阅。
     * ACTIVE. 订阅处于活动状态。
     * SUSPENDED. 订阅被暂停。
     * CANCELLED. 订阅被取消。
     * EXPIRED. 订阅已过期。
     */
    private String status;

    public static final String STATUS_ACTIVE = "ACTIVE";
    //订阅id
    private String id;
    //原样返回域
    private String custom_id;
    //计划id
    //private String plan_id;
    //数量
    //private Integer quantity;
    private BillingInfo billing_info;



   /* {
        "status":"ACTIVE", "status_update_time":"2022-03-23T05:43:10Z", "id":"I-7WWEU80TX0V8", "plan_id":
        "P-995946860K3276457MI4EYIY", "start_time":"2022-03-23T05:42:09Z", "quantity":"1", "shipping_amount":{
        "currency_code":"USD", "value":"0.0"
    },"subscriber":{
        "email_address":"sb-cqejq14242058@personal.example.com", "payer_id":"TS5FKHDCQT9LW", "name":{
            "given_name":"John", "surname":"Doe"
        },"shipping_address":{
            "address":{
                "address_line_1":"NO 1 Nan Jin Road", "admin_area_2":"Shanghai", "admin_area_1":
                "Shanghai", "postal_code":"200000", "country_code":"C2"
            }
        }
    },"billing_info":{
        "outstanding_balance":{
            "currency_code":"USD", "value":"0.0"
        },"cycle_executions":[{
            "tenure_type":"REGULAR", "sequence":1, "cycles_completed":1, "cycles_remaining":
            11, "current_pricing_scheme_version":1, "total_cycles":12
        }],"last_payment":{
            "amount":{
                "currency_code":"USD", "value":"120.0"
            },"time":"2022-03-23T05:43:10Z"
        },"next_billing_time":"2022-04-23T10:00:00Z", "final_payment_time":
        "2023-02-23T10:00:00Z", "failed_payments_count":0
    },"create_time":"2022-03-23T05:43:09Z", "update_time":"2022-03-23T05:43:10Z", "custom_id":"7", "plan_overridden":
        false, "links":[{
        "href":"https://api.sandbox.paypal.com/v1/billing/subscriptions/I-7WWEU80TX0V8/cancel", "rel":"cancel", "method":
        "POST"
    },{
        "href":"https://api.sandbox.paypal.com/v1/billing/subscriptions/I-7WWEU80TX0V8", "rel":"edit", "method":"PATCH"
    },{
        "href":"https://api.sandbox.paypal.com/v1/billing/subscriptions/I-7WWEU80TX0V8", "rel":"self", "method":"GET"
    },{
        "href":"https://api.sandbox.paypal.com/v1/billing/subscriptions/I-7WWEU80TX0V8/suspend", "rel":
        "suspend", "method":"POST"
    },{
        "href":"https://api.sandbox.paypal.com/v1/billing/subscriptions/I-7WWEU80TX0V8/capture", "rel":
        "capture", "method":"POST"
    }]}*/

}
