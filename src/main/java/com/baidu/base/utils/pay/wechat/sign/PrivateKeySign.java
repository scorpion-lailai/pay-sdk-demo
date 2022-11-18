package com.baidu.base.utils.pay.wechat.sign;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class PrivateKeySign {

  public static String getToken(
      String appid, String prepay_id, String nonce, Long timeStamp, PrivateKey privateKey)
      throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
    String message = buildMessage(appid, timeStamp, nonce, prepay_id);
    // 签名
    return sign(message.getBytes(StandardCharsets.UTF_8), privateKey);
  }

  public static String sign(byte[] message, PrivateKey privateKey)
      throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    // 签名方式
    Signature sign = Signature.getInstance("SHA256withRSA");
    // 私钥，通过MyPrivateKey来获取，这是个静态类可以接调用方法 ，需要的是_key.pem文件的绝对路径配上文件名
    sign.initSign(privateKey);
    sign.update(message);

    return Base64.getEncoder().encodeToString(sign.sign());
  }

  public static String buildMessage(
      String appid, long timestamp, String nonceStr, String prepay_id) {
    return appid + "\n" + timestamp + "\n" + nonceStr + "\n" + prepay_id + "\n";
  }
}
