package com.baidu.web.po;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName order
 */

@Data
public class Order implements Serializable {
    /**
     *
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 1:待付款 4:已完成 5.退款
     */
    private Integer status;

    public static final int STATUS_PREPARE = 1;
    public static final int STATUS_FINISH = 4;
    public static final int STATUS_REFUND = 5;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 1.vip 2.svip 3.公司购买慧眼币 4.企业增加人数 5.公司购买个人报告 6.个人购买慧眼币 7.会员升级 8.个人购买报告 9.公司报告升级 10.去购买
     */
    private Integer type;

    public static final int TYPE_VIP = 1;
    public static final int TYPE_SVIP = 2;
    public static final int TYPE_COMPANY_BALANCE = 3;
    public static final int TYPE_PEOPLE = 4;
    public static final int TYPE_COMPANY_REPORT = 5;
    public static final int TYPE_PERSON_BALANCE = 6;
    public static final int TYPE_VIP_UP_GRADE = 7;
    public static final int TYPE_PERSON_REPORT = 8;
    public static final int TYPE_COMPANY_REPORT_UPGRADE = 9;
    public static final int TYPE_COMPANY_REPORT_TO_BUY = 10;

    /**
     * 1.钱币 2.时间[单位:年]
     */
    private Integer unit;

    public static final int UNIT_DATE = 2;
    /**
     * 1：微信、2：支付宝、3：苹果支付 4.慧眼币
     */
    private Integer payType;

    public static final int PAY_TYPE_WECHAT = 1;
    public static final int PAY_TYPE_ALIPAY = 2;
    public static final int PAY_TYPE_IOS = 3;
    public static final int PAY_TYPE_BALANCE = 4;

    /**
     * 第三方交易单号
     */
    private String tradeNo;
    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 报告授权id
     */
    private Long reportAuthId;

    /**
     * 购买的报告人的身份证号码
     */
    private String reportIdCard;

    /**
     * 报告购买的姓名
     */
    private String reportUserName;

    /**
     * 0 不开发票 1 开发票
     */
    private Long invoiceId;

    /**
     * 备注
     */
    private String remark;
    /* 报告类型 */
    private Integer reportType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     *
     */
    private Integer valid;

}
