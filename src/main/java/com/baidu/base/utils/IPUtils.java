package com.baidu.base.utils;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {

  protected IPUtils() {}
  public static String getIpAddress(HttpServletRequest request) {
    // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

    String ip = request.getHeader("X-Forwarded-For");

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("X-REAL-IP");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_CLIENT_IP");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
      }
      if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
      }

    } else if (ip.length() > 15) {
      String[] ips = ip.split(",");
      for (String strIp : ips) {
        if (!("unknown".equalsIgnoreCase(strIp))) {
          ip = strIp;
          break;
        }
      }
    }
    return ip;
  }

  public static String getIpAddress() {
    // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
    HttpServletRequest request = WebContextUtil.getRequest();
    return getIpAddress(request);
  }
}
