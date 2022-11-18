package com.baidu.web.controller;

import com.baidu.base.json.Result;
import com.baidu.web.service.AliPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("alipay")
@Api(tags = "支付宝支付")
public class AliPayController {

    @Resource
    private AliPayService alipayService;

    @PostMapping("pc")
    @ApiOperation(value = "支付宝pc支付", notes = "返回结果为form表单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号"),
            @ApiImplicitParam(name = "amount", value = "金额"),
            @ApiImplicitParam(name = "returnUrl", value = "支付成功后的跳转页面"),
    })

    public String alipayPC(Long orderId, String returnUrl, BigDecimal amount) {
        return Result.ok(alipayService.alipayPC(orderId, returnUrl, amount));
    }

    @PostMapping("h5")
    @ApiOperation(value = "支付宝h5支付", notes = "返回结果为form表单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号"),
            @ApiImplicitParam(name = "amount", value = "金额"),
            @ApiImplicitParam(name = "returnUrl", value = "支付成功后的跳转页面"),
    })
    public String alipayH5(Long orderId, String returnUrl, BigDecimal amount) {
        return Result.ok(alipayService.alipayH5(orderId, returnUrl, amount));
    }

    @PostMapping("app")
    @ApiOperation(value = "支付宝app支付", notes = "返回结果直接给前端，即可调起支付SDK")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号"),
    })
    public String alipayAPP(Long orderId, BigDecimal amount) {
        return Result.ok(alipayService.alipayAPP(orderId, amount));
    }

    @PostMapping("alipay/notify")
    @ApiOperation(value = "支付宝--->[新]", hidden = true)
    public String alipayNotify(HttpServletRequest request) {
        try {
            alipayService.alipayNotify(request);
        } catch (Exception e) {
            return "fail";
        }
        return "success";
    }

}
