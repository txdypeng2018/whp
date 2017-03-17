package com.proper.enterprise.isj.proxy.business.opinion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FeedbackStatusContext;
import com.proper.enterprise.isj.function.opinion.FindByFeedbackStatusFunction;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FindUserOpinionByFeedbackStatusBusiness<M extends FeedbackStatusContext<List<ServiceUserOpinionDocument>> & ModifiedResultBusinessContext<List<ServiceUserOpinionDocument>>>
        implements IBusiness<List<ServiceUserOpinionDocument>, M> {

    @Autowired
    FindByFeedbackStatusFunction findUserOpinionByFeedbackStatusFunction;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(findUserOpinionByFeedbackStatusFunction.execute(ctx.getResult()));
    }

}
