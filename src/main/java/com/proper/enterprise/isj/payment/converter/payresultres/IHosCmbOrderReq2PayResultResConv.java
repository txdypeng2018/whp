package com.proper.enterprise.isj.payment.converter.payresultres;

import com.proper.enterprise.isj.payment.converter.AbstractManagedConverter;
import com.proper.enterprise.isj.payment.model.IHosCmbOrderReq;
import com.proper.enterprise.platform.pay.cmb.model.CmbPayResultRes;

public class IHosCmbOrderReq2PayResultResConv
        extends AbstractManagedConverter<Class<Object>, IHosCmbOrderReq, CmbPayResultRes> {

    @Override
    public CmbPayResultRes convert(IHosCmbOrderReq source, CmbPayResultRes target) {
        return new CmbPayResultRes();
    }

}
