package com.proper.enterprise.isj.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.proper.enterprise.isj.context.SimpleFunctionInvokeContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class SimpleFunctionBusiness<T, R, M extends SimpleFunctionInvokeContext<T, R>> implements IBusiness<T, M> {

    @Autowired
    WebApplicationContext wac;
    
    @SuppressWarnings("unchecked")
    @Override
    public void process(M ctx) throws Exception {
        IFunction<R> repo = ctx.getSimpleFunctionObject();

        if (repo == null) {
            repo = (IFunction<R>) wac.getBean(ctx.getSimpleFunctionType());
        }

        ctx.setSimpleFunctionResult(repo.execute(ctx.getSimpleFunctionParams()));

    }

}
