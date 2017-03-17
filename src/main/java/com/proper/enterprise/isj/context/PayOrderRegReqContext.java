package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.req.PayOrderRegReq;

public interface PayOrderRegReqContext<T> extends BusinessContext<T> {
    PayOrderRegReq getPayOrderRegReq();

    void setPayOrderRegReq(PayOrderRegReq payOrderRegReq);
}
