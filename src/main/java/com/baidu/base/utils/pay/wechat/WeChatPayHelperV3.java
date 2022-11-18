package com.baidu.base.utils.pay.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.base.exception.Errors;
import com.baidu.base.exception.ResultException;
import com.baidu.base.utils.DateUtil;
import com.baidu.base.utils.IPUtils;
import com.baidu.base.utils.pay.PayConfig;
import com.baidu.base.utils.pay.NotifyUrl;
import com.baidu.base.utils.pay.PayNotifyParam;
import com.baidu.base.utils.pay.wechat.sign.PrivateKeySign;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class WeChatPayHelperV3 {
    private static final String CURRENT_ENV_URL = PayConfig.currentEnvUrl;
    protected static final SecureRandom RANDOM = new SecureRandom();
    private static final String mchId = PayConfig.wechatPayMchID;
    private static final String privateKeyPath = PayConfig.wechatPayPrivateKeyPath;
    private static final String certPath = PayConfig.wechatPayCertPath;
    private static final String v3key = PayConfig.wechatPayV3Key;
    public static final String serialNo = PayConfig.wechatPaySerialNo;

    public static final String APPID_APP = PayConfig.wechatPayAPPID_APP;
    public static final String APPID_H5 = PayConfig.wechatPayAPPID_H5;
    public static final String APPID_PC = PayConfig.wechatPayAPPID_PC;
    public static final String APPID_MiniProgram = PayConfig.wechatPayAPPID_MiniProgram;

    public static Map<String, String> APP(Long orderId, BigDecimal amount, NotifyUrl notifyUrl) {
        String APPID = APPID_APP;

        CloseableHttpClient httpClient = buildHttp();
        HttpPost httpPost = buildHttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/app");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode
                .put("mchid", mchId)
                .put("appid", APPID)
                .put("description", "APP在线付费")
                .put("notify_url", CURRENT_ENV_URL + notifyUrl.getUrl())
                .put("out_trade_no", orderId.toString());

        amount = (amount.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP));
        rootNode.putObject("amount").put("total", amount.intValue());
        try {
            objectMapper.writeValue(bos, rootNode);
            httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // http repsonse
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity);
            log.info("返回参数为：{}", s);

            JSONObject json = JSON.parseObject(s);
            String prepayId = json.getString("prepay_id");

            // 拼接参数返回APP
            String nonce = generateNonceStr();
            long timeStamp = System.currentTimeMillis() / 1000;

            Map<String, String> result = new HashMap<>();
            result.put("appId", APPID); // 应用appid
            result.put("nonceStr", nonce);
            result.put("timestamp", Long.toString(timeStamp));
            result.put("prepayId", prepayId); // 返回App

            String sign =
                    PrivateKeySign.getToken(APPID, prepayId, nonce, timeStamp, getPrivateKey(privateKeyPath));

            // 签名
            result.put("sign", sign); // 再次签名
            result.put("package", "Sign=WXPay");
            result.put("partnerId", mchId); // 商户号
            return result;
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
    }

    public static Map<String, String> PC(Long orderId, BigDecimal amount, NotifyUrl notifyUrl) {
        CloseableHttpClient httpClient = buildHttp();
        HttpPost httpPost = buildHttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/native");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.createObjectNode();
        //    65秒后过期
        Date date = DateUtil.getAfterDate(new Date(), Calendar.SECOND, 65);
        String formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date);
        log.info(formatDate);
        rootNode
                .put("mchid", mchId)
                .put("appid", APPID_PC)
                .put("description", "PC端在线付费")
                .put("notify_url", CURRENT_ENV_URL + notifyUrl.getUrl())
                .put("out_trade_no", orderId.toString())
                .put("time_expire", formatDate);

        amount = (amount.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP));
        rootNode.putObject("amount").put("total", amount.intValue());
        try {
            objectMapper.writeValue(bos, rootNode);
            httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // http repsonse
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity);
            log.info("返回参数为：{}", s);

            JSONObject json = JSON.parseObject(s);
            String codeUrl = json.getString("code_url");

            // 拼接参数返回APP
            Map<String, String> result = new HashMap<>();
            result.put("codeUrl", codeUrl); // 应用appid
            return result;
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
    }

    public static Map<String, String> H5(Long orderId, BigDecimal amount, NotifyUrl notifyUrl) {
        CloseableHttpClient httpClient = buildHttp();
        HttpPost httpPost = buildHttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/h5");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode
                .put("mchid", mchId)
                .put("appid", APPID_H5)
                .put("description", "移动H5在线付费")
                .put("notify_url", CURRENT_ENV_URL + notifyUrl.getUrl())
                .put("out_trade_no", orderId.toString());

        amount = (amount.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP));
        rootNode.putObject("amount").put("total", amount.intValue());
        ObjectNode sceneInfo = rootNode.putObject("scene_info");
        sceneInfo.put("payer_client_ip", IPUtils.getIpAddress());

        ObjectNode h5InfoNode = sceneInfo.putObject("h5_info");
        h5InfoNode.put("type", "Wap");

        try {
            objectMapper.writeValue(bos, rootNode);
            httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // http repsonse
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity);
            log.info("返回参数为：{}", s);

            JSONObject json = JSON.parseObject(s);
            String h5Url = json.getString("h5_url");

            // 拼接参数返回APP
            Map<String, String> result = new HashMap<>();
            result.put("h5_url", h5Url); // 应用appid
            return result;
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
    }

    public static Map<String, String> MiniProgram(
            Long orderId, BigDecimal amount, NotifyUrl notifyUrl, String openId) {
        String APPID = APPID_MiniProgram;

        CloseableHttpClient httpClient = buildHttp();
        HttpPost httpPost = buildHttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode
                .put("mchid", mchId)
                .put("appid", APPID)
                .put("description", "小程序在线付费")
                .put("notify_url", CURRENT_ENV_URL + notifyUrl.getUrl())
                .put("out_trade_no", orderId.toString());

        amount = (amount.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP));

        rootNode.putObject("amount").put("total", amount.intValue());
        rootNode.putObject("payer").put("openid", openId);

        try {
            objectMapper.writeValue(bos, rootNode);
            httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // http repsonse
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity);
            log.info("返回参数为：{}", s);

            JSONObject json = JSON.parseObject(s);
            String prepayId = json.getString("prepay_id");
            String packageStr = "prepay_id=" + prepayId;
            // 拼接参数返回APP
            String nonce = generateNonceStr();
            long timeStamp = System.currentTimeMillis() / 1000;

            Map<String, String> result = new HashMap<>();
            result.put("appId", APPID); // 应用appid
            result.put("timeStamp", Long.toString(timeStamp));
            result.put("nonceStr", nonce);
            result.put("package", packageStr);
            String sign =
                    PrivateKeySign.getToken(
                            APPID, packageStr, nonce, timeStamp, getPrivateKey(privateKeyPath));
            // 签名
            result.put("paySign", sign); // 再次签名
            result.put("signType", "RSA"); // 再次签名

            return result;
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
    }

    public static PayNotifyParam wechatNotify(HttpServletRequest request) {
        StringBuilder signStr = new StringBuilder();
        signStr.append(request.getHeader("Wechatpay-Timestamp")).append("\n");
        signStr.append(request.getHeader("Wechatpay-Nonce")).append("\n");

        try {
            BufferedReader br = request.getReader();
            String str;
            StringBuilder builder = new StringBuilder();

            while ((str = br.readLine()) != null) {
                builder.append(str);
            }
            signStr.append(builder).append("\n");
            // 进行验签，确保请求来自微信
            if (!signVerify(
                    request.getHeader("Wechatpay-Serial"),
                    signStr.toString(),
                    request.getHeader("Wechatpay-Signature"))) {
                throw new ResultException(Errors.ERROR);
            }

            // 解密报文
            String info = decryptOrder(builder.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(info);
            // 可以解密出很多参数，具体见[官方文档](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_5_5.shtml)
            String outTradeNo =
                    node.get("out_trade_no")
                            .toString()
                            .substring(1, node.get("out_trade_no").toString().length() - 1);

            String transactionId =
                    node.get("transaction_id")
                            .toString()
                            .substring(1, node.get("transaction_id").toString().length() - 1);

            String payTime =
                    node.get("success_time")
                            .toString()
                            .substring(1, node.get("success_time").toString().length() - 1);

            PayNotifyParam payNotifyParam = new PayNotifyParam();
            payNotifyParam.setOrderId(Long.valueOf(outTradeNo));
            payNotifyParam.setTradeNo(transactionId);
            payNotifyParam.setPayTime(payTime);
            //      payNotifyParam.setPayMoney(new BigDecimal(payMoney));
            payNotifyParam.setTradeStatus(PayNotifyParam.TRADE_STATUS_SUCCESS);
            return payNotifyParam;
        } catch (IOException e) {
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
    }

    public static PrivateKey getPrivateKey(String fileName) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(fileName);
            InputStream inputStream = classPathResource.getInputStream();
            return PemUtil.loadPrivateKey(inputStream);
        } catch (Exception e) {
            // 抛出异常，并把错误文件继续向上抛出
            throw new RuntimeException("私钥文件不存在", e);
        }
    }

    public static ArrayList<X509Certificate> getWXCert(String... fileNames) {
        try {
            ArrayList<X509Certificate> x509Certificates = new ArrayList<>();
            for (String fileName : fileNames) {
                X509Certificate x509Certificate =
                        PemUtil.loadCertificate(new ClassPathResource(fileName).getInputStream());
                x509Certificates.add(x509Certificate);
            }
            return x509Certificates;
        } catch (Exception e) {
            // 抛出异常，并把错误文件继续向上抛出
            throw new RuntimeException("证书文件不存在", e);
        }
    }

    /*支付通知和退款通知给服务器的回调 请求头验签*/
    public static boolean signVerify(String serialNumber, String message, String signature) {
        try {
            // 获取证书管理器实例
            CertificatesManager certificatesManager = CertificatesManager.getInstance();
            // 向证书管理器增加需要自动更新平台证书的商户信息
            certificatesManager.putMerchant(
                    mchId,
                    new WechatPay2Credentials(
                            mchId, new PrivateKeySigner(serialNo, getPrivateKey(privateKeyPath))),
                    v3key.getBytes(StandardCharsets.UTF_8));
            // 从证书管理器中获取verifier
            Verifier verifier = certificatesManager.getVerifier(mchId);
            return verifier.verify(serialNumber, message.getBytes(StandardCharsets.UTF_8), signature);
        } catch (HttpCodeException | NotFoundException | IOException | GeneralSecurityException e) {
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
    }

    /*支付通知和退款通知给服务器的回调 解密报文*/
    public static String decryptOrder(String body) {
        try {
            AesUtil util = new AesUtil(v3key.getBytes(StandardCharsets.UTF_8));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(body);
            JsonNode resource = node.get("resource");
            String ciphertext = resource.get("ciphertext").textValue();
            String associatedData = resource.get("associated_data").textValue();
            String nonce = resource.get("nonce").textValue();
            return util.decryptToString(
                    associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8),
                    ciphertext);
        } catch (JsonProcessingException | GeneralSecurityException e) {
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
    }

    // 构建HTTPClient
    public static CloseableHttpClient buildHttp() {
        WechatPayHttpClientBuilder builder =
                WechatPayHttpClientBuilder.create()
                        .withMerchant(mchId, serialNo, getPrivateKey(privateKeyPath))
                        .withWechatPay(getWXCert(certPath));
        return builder.build();
    }

    // 构建HTTP POST
    public static HttpPost buildHttpPost(String url) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        return httpPost;
    }

    // 生成随机数
    protected static String generateNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] =
                    "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                            .charAt(
                                    RANDOM.nextInt(
                                            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length()));
        }
        return new String(nonceChars);
    }
}
