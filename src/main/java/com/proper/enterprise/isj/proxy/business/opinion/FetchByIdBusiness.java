package com.proper.enterprise.isj.proxy.business.opinion;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.OpinionIdContext;
import com.proper.enterprise.isj.function.opinion.FindByIdFunction;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;

@Service
public class FetchByIdBusiness<M extends OpinionIdContext<ServiceUserOpinionDocument> & ModifiedResultBusinessContext<ServiceUserOpinionDocument>>
        implements IBusiness<ServiceUserOpinionDocument, M> {

    FindByIdFunction functions;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(FunctionUtils.invoke(functions, ctx.getOpinionId()));
    }

}
