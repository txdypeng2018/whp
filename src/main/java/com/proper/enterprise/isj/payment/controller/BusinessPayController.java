package com.proper.enterprise.isj.payment.controller;

import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.payment.service.BusinessPayService;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.api.pay.enums.PayResType;
import com.proper.enterprise.platform.api.pay.model.PayResultRes;
import com.proper.enterprise.platform.api.pay.model.PrepayReq;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.pay.ali.model.AliOrderReq;
import com.proper.enterprise.platform.pay.ali.model.AliPayResultRes;
import com.proper.enterprise.platform.pay.ali.service.AliPayService;
import com.proper.enterprise.platform.pay.cmb.model.CmbOrderReq;
import com.proper.enterprise.platform.pay.cmb.model.CmbPayResultRes;
import com.proper.enterprise.platform.pay.cmb.service.CmbPayService;
import com.proper.enterprise.platform.pay.wechat.model.WechatOrderReq;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayResultRes;
import com.proper.enterprise.platform.pay.wechat.service.WechatPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付Controller.
 */
@RestController
@RequestMapping(value = "/pay")
public class BusinessPayController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessPayController.class);

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    BusinessPayService businessPayService;

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private CmbPayService cmbPayService;

    /**
     * 支付宝预支付处理.
     *
     * @param aliReq 支付宝预支付请求对象.
     * @return 处理结果.
     * @throws Exception 异常.
     */
    @PostMapping(value = "/ali/prepay", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AliPayResultRes> prepareAli(@RequestBody AliOrderReq aliReq) throws Exception {
        AliPayResultRes resObj = new AliPayResultRes();
        try {
            PrepayReq prepayReq = new PrepayReq();
            // 判断支付是否超时
            PayResultRes checkRes = businessPayService.getPayTimeRes(aliReq.getOutTradeNo(), prepayReq);
            if(checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                return responseOfPost(resObj);
            }
            // 判断当前订单是否已经支付过
            checkRes = businessPayService.saveCheckOrder(PayChannel.ALIPAY, aliReq.getOutTradeNo(), aliReq.getTotalFee());
            if(checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                return responseOfPost(resObj);
            }
            // 预支付操作
            PayService payService = (PayService) aliPayService;
            // 订单号
            prepayReq.setOutTradeNo(aliReq.getOutTradeNo());
            // 订单金额
            prepayReq.setTotalFee(aliReq.getTotalFee());
            // 支付用途
            prepayReq.setPayIntent(aliReq.getBody());
            // 支付方式
            prepayReq.setPayWay(BusinessPayConstants.ISJ_PAY_WAY_ALI);
            // 获取预支付信息
            PayResultRes res = payService.savePrepay(prepayReq);
            // 判断预支付结果
            if (res.getResultCode().equals(PayResType.SUCCESS)) {
                resObj = (AliPayResultRes) res;
            } else {
                resObj.setResultCode(res.getResultCode());
                resObj.setResultMsg(res.getResultMsg());
            }
        } catch (Exception e) {
            LOGGER.debug("PayController.prepareAli[Exception]:{}", e);
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        // 返回结果
        return responseOfPost(resObj);
    }

    /**
     * 微信预支付处理.
     *
     * @param wechatReq 微信预支付请求对象.
     * @return 处理结果.
     * @throws Exception 异常.
     */
    @PostMapping(value = "/wechat/prepay", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<WechatPayResultRes> prepayWechat(@RequestBody WechatOrderReq wechatReq) throws Exception {
        WechatPayResultRes resObj = new WechatPayResultRes();
        try {
            PrepayReq prepayReq = new PrepayReq();
            // 判断支付是否超时
            PayResultRes checkRes = businessPayService.getPayTimeRes(wechatReq.getOutTradeNo(), prepayReq);
            if(checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                return responseOfPost(resObj);
            }
            // 判断当前订单是否已经支付过
            checkRes = businessPayService.saveCheckOrder(PayChannel.WECHATPAY,
                    wechatReq.getOutTradeNo(), String.valueOf(wechatReq.getTotalFee()));
            if(checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                return responseOfPost(resObj);
            }
            PayService payService = (PayService) wechatPayService;
            // 订单号
            prepayReq.setOutTradeNo(wechatReq.getOutTradeNo());
            // 订单金额
            prepayReq.setTotalFee(String.valueOf(wechatReq.getTotalFee()));
            // 支付用途
            prepayReq.setPayIntent(wechatReq.getBody());
            // 支付方式
            prepayReq.setPayWay(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
            // 获取预支付信息
            PayResultRes res = payService.savePrepay(prepayReq);
            // 判断预支付结果
            if(PayResType.SUCCESS.equals(res.getResultCode())) {
                resObj = (WechatPayResultRes) res;
            } else {
                resObj.setResultCode(res.getResultCode());
                resObj.setResultMsg(res.getResultMsg());
            }
        } catch (Exception e) {
            LOGGER.debug("PayController.prepayWechat[Exception]:{}", e);
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        // 返回结果
        return responseOfPost(resObj);
    }

    /**
     * 一网通预支付处理.
     *
     * @param cmbReq 一网通预支付请求对象.
     * @return 处理结果.
     * @throws Exception 异常.
     */
    @PostMapping(value = "/cmb/prepay", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CmbPayResultRes> prepayCmb(@RequestBody CmbOrderReq cmbReq) throws Exception {
        CmbPayResultRes resObj = new CmbPayResultRes();
        try {
            PrepayReq prepayReq = new PrepayReq();
            // 判断支付是否超时
            PayResultRes checkRes = businessPayService.getPayTimeRes(cmbReq.getBillNo(), prepayReq);
            if(checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                return responseOfPost(resObj);
            }
            // 判断当前订单是否已经支付过
            checkRes = businessPayService.saveCheckOrder(PayChannel.WEB_UNION, cmbReq.getBillNo(), cmbReq.getAmount());
            if(checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                return responseOfPost(resObj);
            }
            PayService payService = (PayService) cmbPayService;
            // 订单号
            prepayReq.setOutTradeNo(cmbReq.getBillNo());
            // 需要先进行查询,查询用户信息绑定的协议号,如果没有签署协议则需要生成新的协议号
            User currentUser = userService.getCurrentUser();
            BasicInfoDocument basicInfo = userInfoService.getUserInfoByUserId(currentUser.getId());
            // 用户ID
            prepayReq.setUserId(basicInfo.getId());
            // 订单金额
            prepayReq.setTotalFee(String.valueOf(cmbReq.getAmount()));
            // 支付用途
            prepayReq.setPayIntent(cmbReq.getMerchantPara());
            // 支付方式
            prepayReq.setPayWay(BusinessPayConstants.ISJ_PAY_WAY_CMB);
            // 获取预支付信息
            PayResultRes res = payService.savePrepay(prepayReq);
            // 判断预支付结果
            if(res.getResultCode().equals(PayResType.SUCCESS)) {
                resObj = (CmbPayResultRes) res;
            } else {
                resObj.setResultCode(res.getResultCode());
                resObj.setResultMsg(res.getResultMsg());
            }
        } catch (Exception e) {
            LOGGER.debug("PayController.prepayCmb[Exception]:{}", e);
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        // 返回结果
        return responseOfPost(resObj);
    }
}