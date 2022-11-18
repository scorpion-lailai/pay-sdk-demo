package com.baidu.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.baidu.base.json.Result;
import com.baidu.web.service.WechatPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("wechatPay")
@Api(tags = "微信支付")
@Slf4j
public class WechatPayController {

    @Resource
    private WechatPayService wechatPayService;

    @PostMapping("app")
    @ApiOperation(value = "微信app支付--->[新]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号"),
            @ApiImplicitParam(name = "amount", value = "金额"),
    })
    public String wechatPayAPP(Long orderId, BigDecimal amount) {
        return Result.ok(wechatPayService.wechatPayAPP(orderId, amount));
    }

    @PostMapping("pc")
    @ApiOperation(value = "微信pc支付--->[新]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号"),
            @ApiImplicitParam(name = "amount", value = "金额"),
    })
    public String wechatPayPC(Long orderId, BigDecimal amount) {
        return Result.ok(wechatPayService.wechatPayPC(orderId, amount));
    }

    @PostMapping("h5")
    @ApiOperation(value = "微信h5支付--->[新]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号"),
            @ApiImplicitParam(name = "amount", value = "金额"),
    })
    public String wechatPayH5(Long orderId, BigDecimal amount) {
        return Result.ok(wechatPayService.wechatPayH5(orderId, amount));
    }

    @PostMapping("miniProgram")
    @ApiOperation(value = "微信小程序支付--->[新]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号"),
            @ApiImplicitParam(name = "amount", value = "金额"),
            @ApiImplicitParam(name = "openId", value = "微信openId"),
    })
    public String wechatPayMiniProgram(Long orderId, String openId, BigDecimal amount) {
        return Result.ok(wechatPayService.wechatPayMiniProgram(orderId, openId, amount));
    }

    @PostMapping("notify")
    @ApiOperation(value = "微信支付回调--->[新]")
    public String wechatNotify(HttpServletRequest request) {
        try {
            wechatPayService.wechatPayNotify(request);
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new RuntimeException();
        }
        return success();
    }

    // 成功报文
    private static String success() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "SUCCESS");
        return jsonObject.toString();
    }


}
