package com.proper.enterprise.isj.payment.business;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.KeyBusinessWrap;
import com.proper.enterprise.isj.context.AliEntityContext;
import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.context.OrderInfoEntityContext;
import com.proper.enterprise.isj.context.OutTradeNoContext;
import com.proper.enterprise.platform.core.business.AbstractGroupBusiness;
import com.proper.enterprise.platform.core.business.strategy.ConditionDecideFilterProcessStrategy;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.isj.support.decide.OrderContextStatusLtUncomfirmDecide;

@Service
public class AliNoticePayOrRegisterBusiness<T, M extends OrderInfoEntityContext<T> & AliEntityContext<T> & OutTradeNoContext<T> & MapParamsContext<T>>
        extends AbstractGroupBusiness<T, M, ArrayList<IBusiness<T, M>>> {

    @Autowired
    AliNoticeRegisterBusiness<T, M> aliNoticeRegisterBusiness;

    @Autowired
    AliNoticePayBusiness<T, M> aliNoticePayBusiness;

    @Autowired
    ConditionDecideFilterProcessStrategy<T, Boolean, M> strategy;

    @SuppressWarnings("rawtypes")
    @Autowired
    FetchOutTradeNoFromParamsBusiness fetchOutTradeNoFromParamsBusiness;
    @SuppressWarnings("rawtypes")
    @Autowired
    FetchAliEntityByParamsBusiness fetchAliEntityByParamsBusiness;
    @SuppressWarnings("rawtypes")
    @Autowired
    FetchOrderByOutTradeNoBusiness fetchOrderByOutTradeNoBusiness;

    @SuppressWarnings("unchecked")
    public AliNoticePayOrRegisterBusiness() {
        super();

        strategy.setDecide(new OrderContextStatusLtUncomfirmDecide<T, M>());
        this.setStrategy(strategy);
        this.addHandler(new KeyBusinessWrap<Boolean, T, M>(Boolean.TRUE, fetchOutTradeNoFromParamsBusiness));
        this.addHandler(new KeyBusinessWrap<Boolean, T, M>(Boolean.TRUE, fetchAliEntityByParamsBusiness));
        this.addHandler(new KeyBusinessWrap<Boolean, T, M>(Boolean.TRUE, fetchOrderByOutTradeNoBusiness));
        this.addHandler(new KeyBusinessWrap<Boolean, T, M>(Boolean.TRUE, aliNoticeRegisterBusiness));

        this.addHandler(new KeyBusinessWrap<Boolean, T, M>(Boolean.FALSE, fetchOutTradeNoFromParamsBusiness));
        this.addHandler(new KeyBusinessWrap<Boolean, T, M>(Boolean.FALSE, fetchAliEntityByParamsBusiness));
        this.addHandler(new KeyBusinessWrap<Boolean, T, M>(Boolean.FALSE, fetchOrderByOutTradeNoBusiness));
        this.addHandler(new KeyBusinessWrap<Boolean, T, M>(Boolean.FALSE, aliNoticePayBusiness));
    }

}
