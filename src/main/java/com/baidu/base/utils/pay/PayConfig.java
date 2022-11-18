package com.baidu.base.utils.pay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//注释请看配置文件
@Component
public class PayConfig {

    // paypal配置
    public static String paypalKey;
    public static String paypalSecret;
    public static String paypalMode;
    public static String paypalBaseUrl;
    public static String paypalReturnUrl;
    public static String paypalCancelUrl;

    //服务器地址
    public static String currentEnvUrl;

    //支付宝配置
    public static String alipayAppID;
    public static String alipayPrivateKey;
    public static String alipayPublicKey;
    public static String alipaySignType;

    //微信配置
    public static String wechatPayMchID;
    public static String wechatPayPrivateKeyPath;
    public static String wechatPayCertPath;
    public static String wechatPayV3Key;
    public static String wechatPaySerialNo;

    public static String wechatPayAPPID_APP;
    public static String wechatPayAPPID_PC;
    public static String wechatPayAPPID_H5;
    public static String wechatPayAPPID_MiniProgram;

    @Value("${pay.config.paypal.key}")
    public void setEndpoint(String paypalKey) {
        PayConfig.paypalKey = paypalKey;
    }

    @Value("${pay.config.paypal.secret}")
    public void setPaypalSecret(String paypalSecret) {
        PayConfig.paypalSecret = paypalSecret;
    }

    @Value("${pay.config.paypal.mode}")
    public void setPaypalMode(String paypalMode) {
        PayConfig.paypalMode = paypalMode;
    }

    @Value("${pay.config.paypal.base.url}")
    public void setPayBaseUrl(String paypalBaseUrl) {
        PayConfig.paypalBaseUrl = paypalBaseUrl;
    }

    @Value("${pay.config.paypal.return.url}")
    public void setPaypalReturnUrl(String paypalReturnUrl) {
        PayConfig.paypalReturnUrl = paypalReturnUrl;
    }

    @Value("${pay.config.paypal.cancel.url}")
    public void setPaypalCancelUrl(String paypalCancelUrl) {
        PayConfig.paypalCancelUrl = paypalCancelUrl;
    }

    @Value("${pay.config.current.env.url}")
    public void setCurrentEnvUrl(String currentEnvUrl) {
        PayConfig.currentEnvUrl = currentEnvUrl;
    }

    @Value("${pay.config.alipay.appid}")
    public void setAlipayAppID(String alipayAppID) {
        PayConfig.alipayAppID = alipayAppID;
    }

    @Value("${pay.config.alipay.private.key}")
    public void setAlipayPrivateKey(String alipayPrivateKey) {
        PayConfig.alipayPrivateKey = alipayPrivateKey;
    }

    @Value("${pay.config.alipay.public.key}")
    public void setAlipayPublicKey(String alipayPublicKey) {
        PayConfig.alipayPublicKey = alipayPublicKey;
    }

    @Value("${pay.config.alipay.sign.type}")
    public void setAlipaySignType(String alipaySignType) {
        PayConfig.alipaySignType = alipaySignType;
    }

    @Value("${pay.config.wechat.mchid}")
    public void setWechatPayMchID(String wechatPayMchID) {
        PayConfig.wechatPayMchID = wechatPayMchID;
    }

    @Value("${pay.config.wechat.privateKey.path}")
    public void setWechatPayPrivateKeyPath(String wechatPayPrivateKeyPath) {
        PayConfig.wechatPayPrivateKeyPath = wechatPayPrivateKeyPath;
    }

    @Value("${pay.config.wechat.cert.path}")
    public void setWechatPayCertPath(String wechatPayCertPath) {
        PayConfig.wechatPayCertPath = wechatPayCertPath;
    }

    @Value("${pay.config.wechat.v3Key}")
    public void setWechatPayV3Key(String wechatPayV3Key) {
        PayConfig.wechatPayV3Key = wechatPayV3Key;
    }

    @Value("${pay.config.wechat.serialNo}")
    public void setWechatPaySerialNo(String wechatPaySerialNo) {
        PayConfig.wechatPaySerialNo = wechatPaySerialNo;
    }

    @Value("${pay.config.wechat.app.appid}")
    public void setWechatPayAPPID_APP(String wechatPayAPPID_APP) {
        PayConfig.wechatPayAPPID_APP = wechatPayAPPID_APP;
    }

    @Value("${pay.config.wechat.pc.appid}")
    public void setWechatPayAPPID_PC(String wechatPayAPPID_PC) {
        PayConfig.wechatPayAPPID_PC = wechatPayAPPID_PC;
    }

    @Value("${pay.config.wechat.h5.appid}")
    public void setWechatPayAPPID_H5(String wechatPayAPPID_H5) {
        PayConfig.wechatPayAPPID_H5 = wechatPayAPPID_H5;
    }

    @Value("${pay.config.wechat.miniProgram.appid}")
    public void setWechatPayAPPID_MiniProgram(String wechatPayAPPID_MiniProgram) {
        PayConfig.wechatPayAPPID_MiniProgram = wechatPayAPPID_MiniProgram;
    }


}
