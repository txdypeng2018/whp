package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.pay.cmb.model.CmbOrderReq;

public interface CmbOrderReqEntityContext<T> extends BusinessContext<T> {
    void setCmbReq(CmbOrderReq cmbOrderReq);

    CmbOrderReq getCmbReq();
}
