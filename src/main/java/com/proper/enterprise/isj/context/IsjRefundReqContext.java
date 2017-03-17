package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.req.RefundReq;

public interface IsjRefundReqContext<T> extends BusinessContext<T> {
    RefundReq getIsjRefundReq();

    void setIsjRefundReq(RefundReq req);
}
