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
public class Name {
    /**
     * 当当事人是一个人时，当事人的全名。
     * 最大值300
     */
    @JsonProperty("full_name")
    private String fullName;
}