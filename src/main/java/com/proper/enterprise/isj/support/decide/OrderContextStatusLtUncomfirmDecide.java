package com.proper.enterprise.isj.support.decide;

import com.proper.enterprise.isj.context.OrderInfoEntityContext;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.platform.core.api.ContextDecide;
import com.proper.enterprise.platform.core.utils.ConfCenter;

public class OrderContextStatusLtUncomfirmDecide<T, M extends OrderInfoEntityContext<T>>
        implements ContextDecide<T, Boolean, M> {

    @Override
    public Boolean decide(M ctx) {
        Order orderinfo = ctx.getOrderinfo();
        return orderinfo.getPaymentStatus() < ConfCenter.getInt("isj.pay.paystatus.unconfirmpay");
    }

}
