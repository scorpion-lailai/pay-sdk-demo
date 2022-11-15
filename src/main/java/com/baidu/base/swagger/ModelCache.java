package com.baidu.base.swagger;

import io.swagger.models.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xzll
 * @version 1.0
 * @date 2021/5/10 14:58
 */
public class ModelCache {

    static Map<String, Model> extra_cache = new HashMap<>();

    static Map<String, Value2<String, String[]>> specified_cache = new HashMap<>();
}
