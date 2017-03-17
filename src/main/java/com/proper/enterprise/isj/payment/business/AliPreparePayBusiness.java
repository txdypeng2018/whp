package com.proper.enterprise.isj.payment.business;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.AliOrderReqEntityContext;
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
import com.proper.enterprise.platform.pay.ali.model.AliOrderReq;
import com.proper.enterprise.platform.pay.ali.model.AliPayResultRes;
import com.proper.enterprise.platform.pay.ali.service.AliPayService;

@Service
public class AliPreparePayBusiness<M extends AliOrderReqEntityContext<AliPayResultRes> & ModifiedResultBusinessContext<AliPayResultRes>>
        implements IBusiness<AliPayResultRes, M>, ILoggable {

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    BusinessPayService businessPayService;

    @Autowired
    private AliPayService aliPayService;

    @Override
    public void process(M ctx) {
        AliOrderReq aliReq = ctx.getAliOrderReq();
        AliPayResultRes resObj = new AliPayResultRes();
        try {
            PrepayReq prepayReq = new PrepayReq();
            // 判断支付是否超时
            PayResultRes checkRes = businessPayService.getPayTimeRes(aliReq.getOutTradeNo(), prepayReq);
            if (checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                ctx.setResult(resObj);
            }
            // 判断当前订单是否已经支付过
            checkRes = businessPayService.saveCheckOrder(PayChannel.ALIPAY, aliReq.getOutTradeNo(),
                    aliReq.getTotalFee());
            if (checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                ctx.setResult(resObj);
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
            debug("PayController.prepareAli[Exception]:{}", e);
            resObj.setResultCode(PayResType.SYSERROR);
            resObj.setResultMsg(CenterFunctionUtils.APP_SYSTEM_ERR);
        }
        // 返回结果
        ctx.setResult(resObj);
    }

}
