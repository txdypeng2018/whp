package com.proper.enterprise.isj.payment.converter.apprefundinfo2refundreq;

import com.proper.enterprise.isj.webservices.model.req.RefundReq;

public interface AppRefundInfo2RefundReqHandler<S> {
    RefundReq convert(S source, String orderNo, String refundId);
}
