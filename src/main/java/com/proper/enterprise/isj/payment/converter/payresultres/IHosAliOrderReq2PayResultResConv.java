package com.proper.enterprise.isj.payment.converter.payresultres;

import com.proper.enterprise.isj.payment.converter.AbstractManagedConverter;
import com.proper.enterprise.isj.payment.model.IHosAliOrderReq;
import com.proper.enterprise.platform.pay.ali.model.AliPayResultRes;

public class IHosAliOrderReq2PayResultResConv extends AbstractManagedConverter<Class<Object>, IHosAliOrderReq, AliPayResultRes> {

    @Override
    public AliPayResultRes convert(IHosAliOrderReq source, AliPayResultRes target) {
        return new AliPayResultRes();
    }

}
