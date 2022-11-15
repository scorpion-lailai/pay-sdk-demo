/**
  * Copyright 2021 json.cn 
  */
package com.baidu.web.vo.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auto-generated: 2021-10-11 20:5:48
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    /**
     * 地址的第一行。
     * 例如，数字或街道。例如，德鲁里巷173号。需要进行数据录入、合规和风险检查。必须包含完整地址。
     */
    @JsonProperty("address_line_1")
    private String addressLine1;

    /**
     *地址的第二行。例如，套房或公寓号码。
     */
    @JsonProperty("address_line_2")
    private String addressLine2;

    /**
     * 管理区域2
     * 一个城市、城镇或村庄. 小于管理区一级。
     */
    @JsonProperty("admin_area_2")
    private String adminArea2;

    /**
     * 管理区域1
     * 一个国家的最高级别分区，通常是一个省、州或 ISO-3166-2分区。邮递服务格式。例如，加利福尼亚，而不是加利福尼亚。按国家分列的价值是: 英国。一个县。美国。一个国家。加拿大。一个省。日本。一个县。瑞士。一个坎顿。
     * UK. A county.
     * US. A state.
     * Canada. A province.
     * Japan. A prefecture.
     */
    @JsonProperty("admin_area_1")
    private String adminArea1;

    /**
     * 邮政编码 即邮政编码或等效邮政编码。
     */
    @JsonProperty("postal_code")
    private String postalCode;

    /**
     * 国家代码 required
     * 两个字符的 ISO 3166-1代码，用于标识国家或地区。注: 大不列颠的国家代码是 GB，而不是英国的顶级域名称中使用的英国。使用 C2国家代码为中国全球可比无控制价格(CUP)方法，银行卡，和跨境交易。
     * 参考：https://developer.paypal.com/docs/api/reference/country-codes/
     */
    @JsonProperty("country_code")
    private String countryCode;


}