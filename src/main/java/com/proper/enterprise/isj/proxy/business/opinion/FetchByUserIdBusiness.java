package com.proper.enterprise.isj.proxy.business.opinion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.UserIdContext;
import com.proper.enterprise.isj.function.opinion.FetchByUserIdFunction;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;

@Service
public class FetchByUserIdBusiness<M extends UserIdContext<List<ServiceUserOpinionDocument>> & ModifiedResultBusinessContext<List<ServiceUserOpinionDocument>>>
        implements IBusiness<List<ServiceUserOpinionDocument>, M> {

    @Autowired
    FetchByUserIdFunction func;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(FunctionUtils.invoke(func, ctx.getUserId()));
    }

}