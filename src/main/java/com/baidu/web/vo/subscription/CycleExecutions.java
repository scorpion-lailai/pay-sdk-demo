package com.baidu.web.vo.subscription;

import lombok.Data;

@Data
public class CycleExecutions {
    //计费周期的类型:[REGULAR].定期计费周期  [TRIAL].试用计费周期
    private String tenure_type;

    public static final String TENURE_TYPE_REGULAR ="REGULAR";
    public static final String TENURE_TYPE_TRIAL="TRIAL";

    //在其他计费周期中运行此周期的顺序。
    private Integer sequence;

    //已完成的计费周期数。
    private Integer cycles_completed;

    //对于有限的计费周期，cycles_remaining 是剩余周期数。对于无限计费周期，cycles_remaining 设置为 0。
    private Integer cycles_remaining;

    //此计费周期执行的次数。试用计费周期只能执行有限次数
    private Integer total_cycles;


}
