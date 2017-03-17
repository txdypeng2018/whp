package com.proper.enterprise.isj.proxy.business.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class GetPhoneNumBusiness<M extends ModifiedResultBusinessContext<String>> implements IBusiness<String, M> {
    @Autowired
    ServiceService serviceService;

    @Override
    public void process(M ctx) throws Exception {
        String retValue = serviceService.getPhoneNum();
        ctx.setResult(retValue);
    }
}
