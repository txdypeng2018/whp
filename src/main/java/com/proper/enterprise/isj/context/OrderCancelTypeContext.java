package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface OrderCancelTypeContext<T> extends BusinessContext<T> {
    OrderCancelTypeEnum getOrderCancelType();
    void setOrderCancelType(OrderCancelTypeEnum orderCancelType);
}
