package com.proper.enterprise.isj.payment.converter.apprefundinfo2refundreq;

import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.cmb.model.CmbRefundNoDupRes;
import com.proper.enterprise.platform.utils.container.spring.KeyAsClassSpringManagedBeanWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CmbRefundNoDupResConvWrap
        extends KeyAsClassSpringManagedBeanWrap<AbstractAppRefundInfo2RefundReqConv<CmbRefundNoDupRes>, AppRefundInfo2RefundReqContext>
        implements AppRefundInfo2RefundReqHandler<CmbRefundNoDupRes> {

    @Autowired
    AppRefundInfo2RefundReqContext manager;

    @Autowired
    CmbRefundNoDupResConv conv;

    @Override
    public AppRefundInfo2RefundReqContext getManager() {
        return manager;
    }

    @Override
    public AbstractAppRefundInfo2RefundReqConv<CmbRefundNoDupRes> getWrappered() {
        return conv;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getKey() {
        return AliRefundRes.class;
    }

    @Override
    public RefundReq convert(CmbRefundNoDupRes source, String orderNo, String refundId) {
        return getWrappered().convert(source, orderNo, refundId);
    }
}