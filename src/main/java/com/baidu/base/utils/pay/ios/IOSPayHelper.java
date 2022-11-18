package com.baidu.base.utils.pay.ios;

import com.alibaba.fastjson.JSONObject;
import com.baidu.base.exception.Errors;
import com.baidu.base.exception.ResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

public class IOSPayHelper {

    // 内购
    private static String certificateUrlTest = "123";

    private static final Logger log = LoggerFactory.getLogger(IOSPayHelper.class);

    /**
     * 重写X509TrustManager
     */
    private static final TrustManager myX509TrustManager =
            new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }
            };

    public static String SSLContext(String url, String certificateCode) {
        StringBuilder sb = new StringBuilder();
        try {
            // 设置SSLContext
            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null, new TrustManager[]{myX509TrustManager}, null);

            // 打开连接
            HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
            // 设置套接工厂
            conn.setSSLSocketFactory(ssl.getSocketFactory());
            // 加入数据
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/json");

            JSONObject obj = new JSONObject();
            obj.put("receipt-data", certificateCode);

            BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());
            buffOutStr.write(obj.toString().getBytes());
            buffOutStr.flush();
            buffOutStr.close();

            // 获取输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            throw new ResultException(Errors.ERROR);
        }
        return sb.toString();
    }

    public static String sendHttpsCoon(
            Long userId, String url, String certificateCode, Integer orderCarId, Integer userType) {
    /* boolean isFlag = false;
        if (url.isEmpty()) {
          return null;
        }

        String sb = SSLContext(certificateUrlTest, certificateCode);
        log.info("------输出ApplePay内购验证结果------" + sb);

        // 转义苹果返回的参数
        JSONObject job = JSONObject.parseObject(sb);

        if ("21007".equals(job.getString("status"))) {
          // 切换沙盒的地址
          url = certificateUrlTest;
          sb = SSLContext(url, certificateCode);
          log.info("------输出ApplePay内购验证结果------" + sb);

          // 转义苹果返回的参数
          job = JSONObject.parseObject(sb);
        }

        if (!"0".equals(job.getString("status"))) {
          return ReturnUtils.toDataJSON(Errors.OK, false);
        }
        log.info("开始解析内购返回的参数");
        String receipt = job.getString("receipt");
        JSONObject returnJson = JSONObject.parseObject(receipt);
        String inApp = returnJson.getString("in_app");
        JSONArray jsonArray = JSONArray.parseArray(inApp);

        log.info("jsonArray size is :{}", jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
          JSONObject jsonObject = jsonArray.getJSONObject(i);
          String productId = jsonObject.getString("product_id");
          log.info("productId is :{}", productId);
          String transactionId = jsonObject.getString("transaction_id");
          log.info("transactionId is :{}", transactionId);
          String purchaseDate = jsonObject.getString("purchase_date");
          // 校验此订单号是否已经成功

          //      if (consumeDetailsService.consumerDetailsIsSuccess(transactionId, userId) != 0) {
          //        log.info("transactionId 不匹配！！！ ");
          //        continue;
          //      }

          // 查询出订单充值慧眼币的套餐
          //      PackagePriceVO packagePriceVO = virtualCoinService.getInfo(productId);
          PackagePriceVO packagePriceVO = null;
          log.info("packagePriceVO is :{}", packagePriceVO);
          if (packagePriceVO == null) {
            log.info("productId 不匹配！！！ ");
            continue;
          }
          log.info("生成订单");
          // 生成订单
          OldOrder oldOrder = new OldOrder();
          oldOrder.setTradeNo(transactionId);
          oldOrder.setPid(packagePriceVO.getId()); // 套餐id
          oldOrder.setPayMoney(packagePriceVO.getPackagePrice());
          oldOrder.setPayCoin(packagePriceVO.getPackagePrice());

          if (userType.equals(UserInfoVO.USER_TYPE_COMPANY)) {
            oldOrder.setOrderType(OldOrder.INVEST_COIN); // 公司充值慧眼币
    //        oldOrder.setCompanyId(userInfo().getCompanyInfo().getId());
          } else {
            oldOrder.setOrderType(OldOrder.PERSON_INVEST_COIN); // 个人充值慧眼币
          }

          oldOrder.setPayType(OldOrder.PAY_TYPE_APPLE); // 苹果支付
          Date date = new Date();
          SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
          String format = sdf.format(date);
          oldOrder.setPayTime(format);
          String orderId = GenerateNum.getId(ServiceCode.ORDER_CODE);
          oldOrder.setUserId(userId);

          oldOrder.setOrderCode(orderId);
          oldOrder.setStatus(OldOrder.ORDER_STATUS_NO_FINISH);
          log.info("开始生成订单" + oldOrder);
          //            oldOrderService.add(oldOrder);

        }
        return ReturnUtils.toDataJSON(Errors.OK, isFlag);*/
        return null;
    }
}
