package com.baidu.base.language;

import com.baidu.base.utils.WebContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
public class LocalSource {

    //    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MessageSource messageSource;

    public String getMessage(String key) {
        return this.getMessage(key, new Object[]{}, getLocal());
    }
    public String getMessage(TranslateText text) {
        return this.getMessage(text.name(), new Object[]{}, getLocal());
    }

    public Locale getLocal() {
        try {
            //从param中获取locale
            HttpServletRequest request = WebContextUtil.getRequest();
            String param = request.getParameter("locale");
            //按照[下划线]进行拆分，首先是语言，然后是国家
            if (!StringUtils.isEmpty(param)) {
                String language = "";
                String country = "";
                String[] split = param.split("_");

                if (split.length == 1) {
                    language = split[0];
                } else if (split.length == 2) {
                    language = split[0];
                    country = split[1];
                }
                return new Locale(language, country);
            }
        } catch (Exception e) {
            //初始化报错是正常的，因为无法获取httpServletRequest
        }
        return Locale.getDefault();
    }

    public String getMessage(String key, String defaultMessage) {
        return this.getMessage(key, null, defaultMessage);
    }

    public String getMessage(String key, String defaultMessage, Locale locale) {
        return this.getMessage(key, null, defaultMessage, locale);
    }

    public String getMessage(String key, Locale locale) {
        return this.getMessage(key, null, "", locale);
    }

    public String getMessage(String key, Object[] args) {
        return this.getMessage(key, args, "");
    }

    public String getMessage(String key, Object[] args, Locale locale) {
        return this.getMessage(key, args, "", locale);
    }

    public String getMessage(String key, Object[] args, String defaultMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.getMessage(key, args, defaultMessage, locale);
    }

    public String getMessage(String key, Object[] args, String defaultMessage, Locale locale) {
        return messageSource.getMessage(key, args, defaultMessage, locale);
    }
}
