package com.proper.enterprise.isj.payment.business;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.WeChatOrderReqContext;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.payment.service.BusinessPayService;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.api.pay.enums.PayResType;
import com.proper.enterprise.platform.api.pay.model.PayResultRes;
import com.proper.enterprise.platform.api.pay.model.PrepayReq;
import com.proper.enterprise.platform.api.pay.service.PayService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.pay.wechat.model.WechatOrderReq;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayResultRes;
import com.proper.enterprise.platform.pay.wechat.service.WechatPayService;

@Service
public class WeChatPreparePayBusiness<M extends WeChatOrderReqContext<WechatPayResultRes> & ModifiedResultBusinessContext<WechatPayResultRes>>
        implements IBusiness<WechatPayResultRes, M>, ILoggable {

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    BusinessPayService businessPayService;

    @Autowired
    private WechatPayService wechatPayService;

    @Override
    public void process(M ctx) throws Exception {
        WechatOrderReq wechatReq = ctx.getWeChatReq();
        WechatPayResultRes resObj = new WechatPayResultRes();
        try {
            PrepayReq prepayReq = new PrepayReq();
            // 判断支付是否超时
            PayResultRes checkRes = businessPayService.getPayTimeRes(wechatReq.getOutTradeNo(), prepayReq);
            if (checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                ctx.setResult(resObj);
            }
            // 判断当前订单是否已经支付过
            checkRes = businessPayService.saveCheckOrder(PayChannel.WECHATPAY, wechatReq.getOutTradeNo(),
                    String.valueOf(wechatReq.getTotalFee()));
            if (checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                ctx.setResult(resObj);
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
            if (PayResType.SUCCESS.equals(res.getResultCode())) {
                resObj = (WechatPayResultRes) res;
            } else {
                resObj.setResultCode(res.getResultCode());
                resObj.setResultMsg(res.getResultMsg());
            }
        } catch (Exception e) {
            debug("PayController.prepayWechat[Exception]:{}", e);
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        // 返回结果
        ctx.setResult(resObj);
    }

}
