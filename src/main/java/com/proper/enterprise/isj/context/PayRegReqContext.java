package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.req.PayRegReq;

public interface PayRegReqContext<T> extends BusinessContext<T> {
    PayRegReq getPayRegReq();

    void setPayRegReq(PayRegReq payRegReq);
}
