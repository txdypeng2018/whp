package com.proper.enterprise.isj.payment.business;

import org.springframework.beans.factory.annotation.Autowired;

import com.proper.enterprise.isj.context.OrderInfoEntityContext;
import com.proper.enterprise.isj.context.OutTradeNoContext;
import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.isj.order.service.OrderService;
import com.proper.enterprise.platform.core.api.IBusiness;

public class FetchOrderByOutTradeNoBusiness<T, M extends OrderInfoEntityContext<T> & OutTradeNoContext<T>>
        implements IBusiness<T, M> {

    @Autowired
    OrderService orderService;

    @Override
    public void process(M ctx) throws Exception {
        // 取得订单信息
        Order orderinfo = orderService.findByOrderNo(ctx.getOutTradeNo());
        ctx.setOrderinfo(orderinfo);

    }

}
