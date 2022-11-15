package com.baidu.web.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.baidu.base.exception.Errors;
import com.baidu.base.exception.ResultException;
import com.baidu.base.json.JSONUtils;
import com.baidu.web.service.PayPalService;
import com.baidu.web.vo.subscription.ApplicationContext;
import com.baidu.web.vo.subscription.DetailsResponse;
import com.baidu.web.vo.subscription.PaymentMethod;
import com.baidu.web.vo.subscription.SubscriptionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service

public class PayServiceImpl implements PayPalService {

    @Value("${paypal.key}")
    private String PAYPAL_KEY;
    @Value("${paypal.secret}")
    private String PAYPAL_SECRET;
    @Value("${paypal.mode}")
    private String PAYPAL_MODE;
    @Value("${paypal.base.url}")
    private String PAYPAL_BASE_URL;

    //PAYPAL支付取消的跳转url
    public static final String PAYPAL_CANCEL_URL = "https://www.baidu.com/s?wd=支付取消";
    //PAYPAL支付成功后，前端跳转的页面
    public static final String PAYPAL_RETURN_URL = "https://www.baidu.com/s?wd=支付成功";
    //日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void paypalNotify(String paymentId, String payerId) {
        //通过支付的回调再次进行查询支付信息
        APIContext apiContext = new APIContext(PAYPAL_KEY, PAYPAL_SECRET, PAYPAL_MODE);
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        try {
            payment = payment.execute(apiContext, paymentExecute);
            Transaction transaction = payment.getTransactions().get(0);
            // 订单总价
            String total = transaction.getAmount().getTotal();
            //paypal的transactionId，可用于退款
            String transactionId = transaction.getRelatedResources().get(0).getAuthorization().getId();
            //原样返回域
            Long orderId = Long.valueOf(transaction.getCustom());
            //支付完成,写入数据库
            //orderService.finish(orderId, transactionId, new BigDecimal(total));
            logger.info("订单总价为:{},transactionId为:{},orderId:{}", total, transactionId, orderId);
        } catch (Exception e) {
            logger.error("支付失败", e);
            throw new ResultException(Errors.PAY_FAIL);
        }
    }

    /**
     * @param subtotal 商品价格
     * @return 付款链接
     */
    @Override
    public Object paypalOrderPay(BigDecimal subtotal, Long orderId) {
        //  接口请参照：https://developer.paypal.com/api/rest/
        /*
         * 支付流程：
         * 1. 创建Payer对象并设置PaymentMethod
         * 2. 设置RedirectUrls并设置cancelURL和returnURL
         * 3. 设置详细信息并添加PaymentDetails
         * 4. 设置金额
         * 5. 设置交易
         * 6. 添加付款明细并将Intent设置为“授权”
         * 7. 通过传递clientID、secret和mode创建APIContext
         * 8. 创建Payment对象并获取paymentID
         * 9. 将payerID 设置为 PaymentExecution 对象
         * 10.执行支付并获得授权
         */
        BigDecimal shipping = new BigDecimal("0.00");
        BigDecimal tax = new BigDecimal("0.00");
        BigDecimal total = shipping.add(subtotal).add(tax);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // 重定向网址
        RedirectUrls redirectUrls = new RedirectUrls();
        //取消支付url
        redirectUrls.setCancelUrl(PAYPAL_CANCEL_URL);
        //支付成功后，前端跳转页面
        redirectUrls.setReturnUrl(PAYPAL_RETURN_URL);

        // 设置交易金额  1.运费 2.小计 3.税
        Details details = new Details();
        details.setShipping(shipping.toString());
        details.setSubtotal(subtotal.toString());
        details.setTax(tax.toString());

        // 设置付款金额 total = shipping + subtotal + tax
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(total.toString());
        amount.setDetails(details);

        // 设置交易信息
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("desc");
        transaction.setCustom(orderId.toString());
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // 添加交易明细
        Payment payment = new Payment();
        //设置付款意图以授权
        payment.setIntent("authorize");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);

        //传递 clientID、secret 和 mode。 最简单、使用最广泛的选项。
        APIContext apiContext = new APIContext(PAYPAL_KEY, PAYPAL_SECRET, PAYPAL_MODE);
        try {
            payment = payment.create(apiContext);
            logger.info("[paypal支付]-[生成订单]->{}", payment);
        } catch (PayPalRESTException e) {
            logger.error("paypal支付异常", e);
            throw new ResultException(Errors.ERROR);
        }
        List<Links> links = payment.getLinks();
        return getRelLink(links, "approval_url").getHref();
    }

    /*
     *  选择paypal返回的链接，从中筛选，并返回给前端
     *  可选值：[self,approval_url,execute,refund,...]
     */
    public Links getRelLink(List<Links> links, String rel) {
        List<Links> collect = links.stream().filter(
                link -> link.getRel().equals(rel)).collect(Collectors.toList()
        );
        if (collect.isEmpty()) {
            logger.error("未从paypal获取到支付链接");
            throw new ResultException(Errors.ERROR);
        } else
            return collect.get(0);
    }

    @Override
    public String getPaypalToken() {
        String body = HttpRequest.post(PAYPAL_BASE_URL + "/v1/oauth2/token")
                .basicAuth(PAYPAL_KEY, PAYPAL_SECRET)
                .form("grant_type", "client_credentials")
                .execute().body();
        logger.info("[paypal支付]-[获取token]->{}", body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        return jsonObject.get("access_token").toString();
    }

    @Override
    public Object createSubscription(String planId, Long orderId) {
        Map<String, String> map = new HashMap<>(4);
        map.put("Content-Type", "application/json");
        map.put("Authorization", getPaypalToken());

        String string = handlerSubsParam(planId, orderId);
        String body = HttpRequest.post(PAYPAL_BASE_URL + "/v1/billing/subscriptions")
                .addHeaders(map)
                .basicAuth(PAYPAL_KEY, PAYPAL_SECRET)
                .body(string)
                .execute().body();
        logger.info("[paypal支付]-[创建订阅]->{}", body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        List<Links> links = JSONObject.parseArray(jsonObject.get("links").toString(), Links.class);
        return getRelLink(links, "approve").getHref();
    }

    /**
     * 处理订阅参数
     */
    private String handlerSubsParam(String planId, Long orderId) {
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setPlanId(planId);

        subscriptionDTO.setCustomId(orderId.toString());
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.setCancelUrl(PAYPAL_CANCEL_URL);
        applicationContext.setReturnUrl(PAYPAL_RETURN_URL);
        applicationContext.setPaymentMethod(new PaymentMethod());
        subscriptionDTO.setApplicationContext(applicationContext);

        String string;
        try {
            string = new ObjectMapper().writeValueAsString(subscriptionDTO);
        } catch (Exception e) {
            logger.error("创建订阅失败", e);
            throw new ResultException(Errors.ERROR);
        }
        return string;
    }

    @Override
    public void subscriptionNotify(String subscriptionId) {

        DetailsResponse detail = getSubscriptionsDetail(subscriptionId);
        //订单id
        String customId = detail.getCustom_id();
        //订单状态
//        String status = detail.getStatus();
        BigDecimal value = detail.getBilling_info().getLast_payment().getAmount().getValue();
        Long orderId = Long.valueOf(customId);
        //订单完成，写入数据库
        // orderService.finish(orderId, id, value);
        logger.info("orderId为:{},订单金额为:{},订阅ID为:{}", orderId, value, subscriptionId);
    }

    public DetailsResponse getSubscriptionsDetail(String id) {
        Map<String, String> map = new HashMap<>(4);
        map.put("Content-Type", "application/json");
        map.put("Authorization", getPaypalToken());

        String body = HttpRequest.get(PAYPAL_BASE_URL + "/v1/billing/subscriptions/" + id)
                .addHeaders(map)
                .basicAuth(PAYPAL_KEY, PAYPAL_SECRET)
                .body("")
                .execute().body();
        logger.info("[paypal支付]-[订阅详情]->{}", body);
        return JSONUtils.string2JavaBean(body, DetailsResponse.class);
    }


}
