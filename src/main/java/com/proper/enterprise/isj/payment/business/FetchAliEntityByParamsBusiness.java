package com.proper.enterprise.isj.payment.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.AliEntityContext;
import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.pay.ali.entity.AliEntity;
import com.proper.enterprise.platform.pay.ali.service.AliPayService;

@Service
public class FetchAliEntityByParamsBusiness<T, M extends MapParamsContext<T> & AliEntityContext<T>>
        implements IBusiness<T, M> {

    @Autowired
    AliPayService aliPayService;

    @Override
    public void process(M ctx) throws Exception {
        AliEntity aliInfo = aliPayService.getAliNoticeInfo(ctx.getParams());
        ctx.setAliInfo(aliInfo);
    }

}
