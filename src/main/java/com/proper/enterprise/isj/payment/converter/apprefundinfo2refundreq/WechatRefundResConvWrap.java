package com.proper.enterprise.isj.payment.converter.apprefundinfo2refundreq;

import com.proper.enterprise.isj.webservices.model.req.RefundReq;
import com.proper.enterprise.platform.pay.ali.model.AliRefundRes;
import com.proper.enterprise.platform.pay.wechat.model.WechatRefundRes;
import com.proper.enterprise.platform.utils.container.spring.KeyAsClassSpringManagedBeanWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatRefundResConvWrap
        extends KeyAsClassSpringManagedBeanWrap<AbstractAppRefundInfo2RefundReqConv<WechatRefundRes>, AppRefundInfo2RefundReqContext>
        implements AppRefundInfo2RefundReqHandler<WechatRefundRes> {

    @Autowired
    AppRefundInfo2RefundReqContext manager;

    @Autowired
    WechatRefundResConv conv;

    @Override
    public AppRefundInfo2RefundReqContext getManager() {
        return manager;
    }

    @Override
    public AbstractAppRefundInfo2RefundReqConv<WechatRefundRes> getWrappered() {
        return conv;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getKey() {
        return AliRefundRes.class;
    }

    @Override
    public RefundReq convert(WechatRefundRes source, String orderNo, String refundId) {
        return getWrappered().convert(source, orderNo, refundId);
    }
}
