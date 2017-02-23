package com.proper.enterprise.isj.payment.converter.prepayreq;

import com.proper.enterprise.isj.payment.constants.BusinessPayConstants;
import com.proper.enterprise.isj.payment.converter.AbstractManagedConverter;
import com.proper.enterprise.platform.api.pay.model.PrepayReq;
import com.proper.enterprise.platform.pay.wechat.model.WechatOrderReq;

public class WechatOrderReq2PrepayReqConv extends AbstractManagedConverter<Class<Object>, WechatOrderReq, PrepayReq> {

    @Override
    public PrepayReq convert(WechatOrderReq source, PrepayReq target) {
        // 订单号
        target.setOutTradeNo(source.getOutTradeNo());
        // 订单金额
        target.setTotalFee(String.valueOf(source.getTotalFee()));
        // 支付用途
        target.setPayIntent(source.getBody());
        // 支付方式
        target.setPayWay(BusinessPayConstants.ISJ_PAY_WAY_WECHAT);
        return target;
    }

}
