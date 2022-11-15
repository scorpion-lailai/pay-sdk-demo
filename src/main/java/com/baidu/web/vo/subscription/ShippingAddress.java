/**
  * Copyright 2021 json.cn 
  */
package com.baidu.web.vo.subscription;

import lombok.Data;

/**
 * Auto-generated: 2021-10-11 20:5:48
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class ShippingAddress{

    /**
     *要将项目发送给谁的人的名称。仅支持全名属性
     */
    private Name name;

    /**
     * 将货物运往的人的地址。只支持地址 _ 行 _ 1、地址 _ 行 _ 2、 admin _ area _ 1、 admin _ area _ 2、邮政编码和国家代码属性。
     */
    private Address address;

}