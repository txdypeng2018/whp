package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.pay.ali.model.AliOrderReq;

public interface AliOrderReqEntityContext<T> extends BusinessContext<T> {
    AliOrderReq getAliOrderReq();

    void setAliOrderReq(AliOrderReq aliReq);
}
