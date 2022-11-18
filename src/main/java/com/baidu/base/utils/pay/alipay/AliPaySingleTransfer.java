package com.baidu.base.utils.pay.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.baidu.base.utils.pay.PayConfig;

/**
 * 单例模式 支付宝单笔转账到个人账户
 *
 * <p>URL 支付宝网关（固定） https://openapi.alipay.com/gateway.do APP_ID APPID即创建应用后生成 获取见上面创建应用并获取APPID
 * APP_PRIVATE_KEY 开发者应用私钥，由开发者自己生成 获取见上面配置密钥 FORMAT 参数返回格式，只支持json json（固定） CHARSET
 * 请求和签名使用的字符编码格式，支持GBK和UTF-8 开发者根据实际工程编码配置 ALIPAY_PUBLIC_KEY 支付宝公钥，由支付宝生成 获取详见上面配置密钥 SIGN_TYPE
 * 商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2 RSA2
 *
 * @author xzll
 */
public class AliPaySingleTransfer {
    private static final String URL = "https://openapi.alipay.com/gateway.do";
    private static final String APP_ID = PayConfig.alipayAppID;
    private static final String APP_PRIVATE_KEY = PayConfig.alipayPrivateKey;
    private static final String ALIPAY_PUBLIC_KEY = PayConfig.alipayPublicKey;

    private static final String FORMAT = "json";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";

    public static AlipayClient getAlipayClient() {
        return new DefaultAlipayClient(
                URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
    }
    public static String getAlipayPublicKey() {
        return ALIPAY_PUBLIC_KEY;
    }


}
