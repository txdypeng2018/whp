package com.proper.enterprise.isj.proxy.business.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.customservice.FetchServicePhoneNumFunction;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchServicePhoneNumBusiness<M extends ModifiedResultBusinessContext<String>> implements IBusiness<String, M> {

    @Autowired
    FetchServicePhoneNumFunction func;
    
    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(func.execute());
    }

}
