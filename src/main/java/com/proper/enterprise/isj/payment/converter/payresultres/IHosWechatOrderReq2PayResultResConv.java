package com.proper.enterprise.isj.payment.converter.payresultres;

import com.proper.enterprise.isj.payment.converter.AbstractManagedConverter;
import com.proper.enterprise.isj.payment.model.IHosWechatOrderReq;
import com.proper.enterprise.platform.pay.wechat.model.WechatPayResultRes;

public class IHosWechatOrderReq2PayResultResConv
        extends AbstractManagedConverter<Class<Object>, IHosWechatOrderReq, WechatPayResultRes> {

    @Override
    public WechatPayResultRes convert(IHosWechatOrderReq source, WechatPayResultRes target) {
        return new WechatPayResultRes();
    }

}
