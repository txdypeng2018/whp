package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface OrderNumContext<T> extends BusinessContext<T> {
    String getOrderNum();

    void setOrderNum(String orderNum);
}
