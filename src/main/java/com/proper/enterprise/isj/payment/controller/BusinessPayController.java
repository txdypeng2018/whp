package com.proper.enterprise.isj.payment.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.AliOrderReqEntityContext;
import com.proper.enterprise.isj.context.CmbOrderReqEntityContext;
import com.proper.enterprise.isj.context.WeChatOrderReqContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.payment.business.AliPreparePayBusiness;
import com.proper.enterprise.isj.payment.business.WeChatPreparePayBusiness;
import com.proper.enterprise.isj.payment.business.WebUnionPreparePayBusiness;
import com.proper.enterprise.platform.pay.ali.model.AliOrderReq;
import com.proper.enterprise.platform.pay.ali.model.AliPayResultRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbOrderReq;
import com.proper.enterprise.platform.pay.cmb.model.CmbPayResultRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatOrderReq;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayResultRes;

/**
 * 支付Controller.
 */
@RestController
@RequestMapping(value = "/pay")
public class BusinessPayController extends IHosBaseController {
    /**
     * 支付宝预支付处理.
     *
     * @param aliReq 支付宝预支付请求对象.
     * @return 处理结果.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(value = "/ali/prepay", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AliPayResultRes> prepareAli(@RequestBody AliOrderReq aliReq) throws Exception {

        return responseOfPost(toolkit.execute(AliPreparePayBusiness.class,
                ctx -> ((AliOrderReqEntityContext<AliPayResultRes>) ctx).setAliOrderReq(aliReq)));

    }

    /**
     * 微信预支付处理.
     *
     * @param wechatReq 微信预支付请求对象.
     * @return 处理结果.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(value = "/wechat/prepay", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<WechatPayResultRes> prepayWechat(@RequestBody WechatOrderReq wechatReq) throws Exception {
        return responseOfPost(toolkit.execute(WeChatPreparePayBusiness.class,
                c -> ((WeChatOrderReqContext<WechatPayResultRes>) c).setWeChatReq(wechatReq)));
    }

    /**
     * 一网通预支付处理.
     *
     * @param cmbReq 一网通预支付请求对象.
     * @return 处理结果.
     * @throws Exception 异常.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(value = "/cmb/prepay", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CmbPayResultRes> prepayCmb(@RequestBody CmbOrderReq cmbReq) throws Exception {
        return responseOfPost(toolkit.execute(WebUnionPreparePayBusiness.class,
                c -> ((CmbOrderReqEntityContext<CmbPayResultRes>) c).setCmbReq(cmbReq)));
    }
}