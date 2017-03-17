package com.proper.enterprise.isj.proxy.business.opinion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.opinion.FetchAllFunction;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchAllOpinionsBusiness<M extends ModifiedResultBusinessContext<List<ServiceUserOpinionDocument>>> implements IBusiness<List<ServiceUserOpinionDocument>, M> {

    @Autowired
    FetchAllFunction func;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(func.execute());
    }
    

}