package com.baidu.base.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**        
 * Title: Web上下文工具类
 * @author xzll    
 */      
public class WebContextUtil {
	  
	/**     
	 * @description 获取HTTP请求    
	 * @return     
	 */
	public static HttpServletRequest getRequest() {

		return ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
	}
}
