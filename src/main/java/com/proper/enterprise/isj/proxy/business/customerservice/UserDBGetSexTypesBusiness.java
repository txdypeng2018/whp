package com.proper.enterprise.isj.proxy.business.customerservice;

import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;

@Service
public class UserDBGetSexTypesBusiness<M extends ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {
    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(CenterFunctionUtils.getSexCodeMap());
    }
}
