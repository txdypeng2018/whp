package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface OrderInfoEntityContext<T> extends BusinessContext<T> {
    Order getOrderinfo();

    void setOrderinfo(Order orderinfo);
}
