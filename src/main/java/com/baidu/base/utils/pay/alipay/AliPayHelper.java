package com.baidu.base.utils.pay.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.baidu.base.exception.Errors;
import com.baidu.base.exception.ResultException;
import com.baidu.base.utils.pay.PayConfig;
import com.baidu.base.utils.pay.NotifyUrl;
import com.baidu.base.utils.pay.PayNotifyParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AliPayHelper {

    private static final String CURRENT_ENV_URL = PayConfig.currentEnvUrl;
    private static final Logger log = LoggerFactory.getLogger(AliPayHelper.class);

    public static String APP(Long orderId, BigDecimal amount, NotifyUrl notifyUrl) {
        // 设置请求的参数
        JSONObject json = new JSONObject();
        json.put("out_trade_no", orderId.toString());
        json.put("total_amount", amount.toString());
        json.put("subject", "APP在线支付");
        json.put("product_code", "QUICK_MSECURITY_PAY");

        AlipayClient alipayClient = AliPaySingleTransfer.getAlipayClient();
        AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();

        // 回调服务端URL
        alipayRequest.setNotifyUrl(CURRENT_ENV_URL + notifyUrl.getUrl());
        alipayRequest.setBizContent(json.toJSONString());
        // 请求
        String result;
        try {
            result = alipayClient.sdkExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new ResultException(Errors.ERROR);
        }
        return result;
    }

    public static String PC(Long orderId, BigDecimal amount, String returnUrl, NotifyUrl notifyUrl) {
        // 设置请求的参数
        JSONObject json = new JSONObject();
        json.put("out_trade_no", orderId);
        json.put("total_amount", amount);
        json.put("subject", "PC端在线支付");
        json.put("body", "无");
        json.put("product_code", "FAST_INSTANT_TRADE_PAY");

        AlipayClient alipayClient = AliPaySingleTransfer.getAlipayClient();
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        // 支付成功后，页面跳转
        alipayRequest.setReturnUrl(returnUrl);
        // 回调服务端URL
        alipayRequest.setNotifyUrl(CURRENT_ENV_URL + notifyUrl.getUrl());
        alipayRequest.setBizContent(json.toJSONString());
        // 请求
        String result;
        try {
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new ResultException(Errors.ERROR);
        }
        return result;
    }

    public static String H5(Long orderId, BigDecimal amount, String returnUrl, NotifyUrl notifyUrl) {

        // 设置请求的参数
        JSONObject json = new JSONObject();
        json.put("out_trade_no", orderId.toString());
        json.put("total_amount", amount.toString());
        json.put("subject", "移动H5在线支付");
        json.put("product_code", "QUICK_WAP_WAY");

        AlipayClient alipayClient = AliPaySingleTransfer.getAlipayClient();
        AlipayTradeWapPayRequest wap = new AlipayTradeWapPayRequest();

        // 回调服务端URL
        wap.setReturnUrl(returnUrl);
        wap.setNotifyUrl(CURRENT_ENV_URL + notifyUrl.getUrl());
        wap.setBizContent(json.toJSONString());
        // 请求
        String result;
        try {
            result = alipayClient.pageExecute(wap).getBody();

        } catch (AlipayApiException e) {
            log.error(e.getMessage(), e);
            throw new ResultException(Errors.ERROR);
        }
        return result;
    }

    public static PayNotifyParam notifyParamParse(HttpServletRequest request) {
        // 验签
        signCheck(request);
        // 商户订单号
        String orderId =
                new String(
                        request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8);
        // 支付宝交易号
        String tradeNo =
                new String(
                        request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8);
        // 交易状态
        String tradeStatus =
                new String(
                        request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8);
        // 交易金额
        String payMoney =
                new String(
                        request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8);
        // 交易时间
        String payTime =
                new String(
                        request.getParameter("gmt_create").getBytes(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8);

        // 表示支付成功
        if (tradeStatus.equals("TRADE_SUCCESS")) {
            log.info("支付成功,订单Id为:{}", orderId);
        } else {
            log.error("支付失败");
            throw new ResultException(Errors.ERROR);
        }

        PayNotifyParam payNotifyParam = new PayNotifyParam();
        payNotifyParam.setOrderId(Long.valueOf(orderId));
        payNotifyParam.setTradeNo(tradeNo);
        payNotifyParam.setPayTime(payTime);
        payNotifyParam.setPayMoney(new BigDecimal(payMoney));
        payNotifyParam.setTradeStatus(PayNotifyParam.TRADE_STATUS_SUCCESS);
        return payNotifyParam;
    }

    private static void signCheck(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        // 拼装参数
        for (String key : requestParams.keySet()) {
            String[] values = requestParams.get(key);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(key, valueStr);
        }
        log.info("params:{}", params);

        boolean signVerified;
        // 签名比对
        try {
            String publicKey = AliPaySingleTransfer.getAlipayPublicKey();
            signVerified = AlipaySignature.rsaCheckV1(params, publicKey, "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            // 如果验签失败，则抛出异常
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
        // 判断是否验签失败
        if (!signVerified) {
            log.error("支付宝验签失败");
            throw new ResultException(Errors.ERROR);
        }
    }
}
