package com.proper.enterprise.isj.payment.business;

import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.context.OutTradeNoContext;
import com.proper.enterprise.platform.core.api.IBusiness;

public class FetchOutTradeNoFromParamsBusiness<T, M extends MapParamsContext<T> & OutTradeNoContext<T>>
        implements IBusiness<T, M> {

    @Override
    public void process(M ctx) throws Exception {
        ctx.setOutTradeNo(ctx.getParams().get("out_trade_no"));
    }

}
