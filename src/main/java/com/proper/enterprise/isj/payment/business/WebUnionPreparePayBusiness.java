package com.proper.enterprise.isj.payment.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.CmbOrderReqEntityContext;
import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.payment.service.BusinessPayService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
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
import com.proper.enterprise.platform.pay.cmb.model.CmbOrderReq;
import com.proper.enterprise.platform.pay.cmb.model.CmbPayResultRes;
import com.proper.enterprise.platform.pay.cmb.service.CmbPayService;

@Service
public class WebUnionPreparePayBusiness<M extends CmbOrderReqEntityContext<CmbPayResultRes> & ModifiedResultBusinessContext<CmbPayResultRes>>
        implements IBusiness<CmbPayResultRes, M> {
    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    BusinessPayService businessPayService;

    @Autowired
    private CmbPayService cmbPayService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUnionPreparePayBusiness.class);

    @Override
    public void process(M ctx) throws Exception {
        CmbOrderReq cmbReq = ctx.getCmbReq();
        CmbPayResultRes resObj = new CmbPayResultRes();
        try {
            PrepayReq prepayReq = new PrepayReq();
            // 判断支付是否超时
            PayResultRes checkRes = businessPayService.getPayTimeRes(cmbReq.getBillNo(), prepayReq);
            if (checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                ctx.setResult(resObj);
            }
            // 判断当前订单是否已经支付过
            checkRes = businessPayService.saveCheckOrder(PayChannel.WEB_UNION, cmbReq.getBillNo(), cmbReq.getAmount());
            if (checkRes.getResultCode() != null && checkRes.getResultCode().equals(PayResType.SYSERROR)) {
                BeanUtils.copyProperties(checkRes, resObj);
                ctx.setResult(resObj);
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
            if (res.getResultCode().equals(PayResType.SUCCESS)) {
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
        ctx.setResult(resObj);
    }
}
