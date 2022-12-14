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
                .put("description", "APP????????????")
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
            log.info("??????????????????{}", s);

            JSONObject json = JSON.parseObject(s);
            String prepayId = json.getString("prepay_id");

            // ??????????????????APP
            String nonce = generateNonceStr();
            long timeStamp = System.currentTimeMillis() / 1000;

            Map<String, String> result = new HashMap<>();
            result.put("appId", APPID); // ??????appid
            result.put("nonceStr", nonce);
            result.put("timestamp", Long.toString(timeStamp));
            result.put("prepayId", prepayId); // ??????App

            String sign =
                    PrivateKeySign.getToken(APPID, prepayId, nonce, timeStamp, getPrivateKey(privateKeyPath));

            // ??????
            result.put("sign", sign); // ????????????
            result.put("package", "Sign=WXPay");
            result.put("partnerId", mchId); // ?????????
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
        //    65????????????
        Date date = DateUtil.getAfterDate(new Date(), Calendar.SECOND, 65);
        String formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date);
        log.info(formatDate);
        rootNode
                .put("mchid", mchId)
                .put("appid", APPID_PC)
                .put("description", "PC???????????????")
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
            log.info("??????????????????{}", s);

            JSONObject json = JSON.parseObject(s);
            String codeUrl = json.getString("code_url");

            // ??????????????????APP
            Map<String, String> result = new HashMap<>();
            result.put("codeUrl", codeUrl); // ??????appid
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
                .put("description", "??????H5????????????")
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
            log.info("??????????????????{}", s);

            JSONObject json = JSON.parseObject(s);
            String h5Url = json.getString("h5_url");

            // ??????????????????APP
            Map<String, String> result = new HashMap<>();
            result.put("h5_url", h5Url); // ??????appid
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
                .put("description", "?????????????????????")
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
            log.info("??????????????????{}", s);

            JSONObject json = JSON.parseObject(s);
            String prepayId = json.getString("prepay_id");
            String packageStr = "prepay_id=" + prepayId;
            // ??????????????????APP
            String nonce = generateNonceStr();
            long timeStamp = System.currentTimeMillis() / 1000;

            Map<String, String> result = new HashMap<>();
            result.put("appId", APPID); // ??????appid
            result.put("timeStamp", Long.toString(timeStamp));
            result.put("nonceStr", nonce);
            result.put("package", packageStr);
            String sign =
                    PrivateKeySign.getToken(
                            APPID, packageStr, nonce, timeStamp, getPrivateKey(privateKeyPath));
            // ??????
            result.put("paySign", sign); // ????????????
            result.put("signType", "RSA"); // ????????????

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
            // ???????????????????????????????????????
            if (!signVerify(
                    request.getHeader("Wechatpay-Serial"),
                    signStr.toString(),
                    request.getHeader("Wechatpay-Signature"))) {
                throw new ResultException(Errors.ERROR);
            }

            // ????????????
            String info = decryptOrder(builder.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(info);
            // ???????????????????????????????????????[????????????](https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_5_5.shtml)
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
            // ???????????????????????????????????????????????????
            throw new RuntimeException("?????????????????????", e);
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
            // ???????????????????????????????????????????????????
            throw new RuntimeException("?????????????????????", e);
        }
    }

    /*???????????????????????????????????????????????? ???????????????*/
    public static boolean signVerify(String serialNumber, String message, String signature) {
        try {
            // ???????????????????????????
            CertificatesManager certificatesManager = CertificatesManager.getInstance();
            // ?????????????????????????????????????????????????????????????????????
            certificatesManager.putMerchant(
                    mchId,
                    new WechatPay2Credentials(
                            mchId, new PrivateKeySigner(serialNo, getPrivateKey(privateKeyPath))),
                    v3key.getBytes(StandardCharsets.UTF_8));
            // ???????????????????????????verifier
            Verifier verifier = certificatesManager.getVerifier(mchId);
            return verifier.verify(serialNumber, message.getBytes(StandardCharsets.UTF_8), signature);
        } catch (HttpCodeException | NotFoundException | IOException | GeneralSecurityException e) {
            log.error(e.toString(), e);
            throw new ResultException(Errors.ERROR);
        }
    }

    /*???????????????????????????????????????????????? ????????????*/
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

    // ??????HTTPClient
    public static CloseableHttpClient buildHttp() {
        WechatPayHttpClientBuilder builder =
                WechatPayHttpClientBuilder.create()
                        .withMerchant(mchId, serialNo, getPrivateKey(privateKeyPath))
                        .withWechatPay(getWXCert(certPath));
        return builder.build();
    }

    // ??????HTTP POST
    public static HttpPost buildHttpPost(String url) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        return httpPost;
    }

    // ???????????????
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
