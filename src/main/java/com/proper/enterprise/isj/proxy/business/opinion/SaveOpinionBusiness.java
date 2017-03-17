package com.proper.enterprise.isj.proxy.business.opinion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.ServiceUserOpinionDocumentContext;
import com.proper.enterprise.isj.function.opinion.SaveFunction;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class SaveOpinionBusiness<M extends ServiceUserOpinionDocumentContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    SaveFunction saveOpinionFunction;
    @Override
    public void process(M ctx) throws Exception {
        saveOpinionFunction.execute(ctx.getOpinionDocment());
    }

}
