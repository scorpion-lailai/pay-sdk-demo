package com.baidu.base.json;

import com.google.gson.Gson;

public class JSONUtils {

    //JavaBean 转化Json
    public static String objectForJson(Object obj) {
        return new Gson().toJson(obj);
    }

    public static <T> T string2JavaBean(String str, Class<T> clazz) {
        return new Gson().fromJson(str, clazz);
    }


}
