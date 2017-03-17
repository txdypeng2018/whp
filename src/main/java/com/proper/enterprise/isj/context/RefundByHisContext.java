package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.isj.webservices.model.res.refundbyhis.RefundByHis;

public interface RefundByHisContext<T> extends BusinessContext<T> {

    RefundByHis getRefundByHis();
    void setRefundByHis(RefundByHis refundByHis);
}
