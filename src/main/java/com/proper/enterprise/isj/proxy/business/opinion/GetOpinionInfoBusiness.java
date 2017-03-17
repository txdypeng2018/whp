package com.proper.enterprise.isj.proxy.business.opinion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.OpinionIdContext;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class GetOpinionInfoBusiness<M extends OpinionIdContext<ServiceUserOpinionDocument> & ModifiedResultBusinessContext<ServiceUserOpinionDocument>>
        implements IBusiness<ServiceUserOpinionDocument, M> {
    @Autowired
    ServiceService serviceService;

    @Override
    public void process(M ctx) throws Exception {
        String opinionId = ctx.getOpinionId();
        ServiceUserOpinionDocument opinion = serviceService.getById(opinionId);
        ctx.setResult(opinion);
    }
}
