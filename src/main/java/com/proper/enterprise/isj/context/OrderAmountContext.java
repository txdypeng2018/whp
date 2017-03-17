package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface OrderAmountContext<T> extends BusinessContext<T> {
    String getOrderAmount();

    void setOrderAmount(String amount);
}
