package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface OutTradeNoContext<T> extends BusinessContext<T> {
    String getOutTradeNo();

    void setOutTradeNo(String outTradeNo);
}
