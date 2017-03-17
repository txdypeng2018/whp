package com.proper.enterprise.isj.proxy.business.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.StopRegRecordDocumentContext;
import com.proper.enterprise.isj.function.registration.SaveStopRegRecordFunction;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.isj.support.function.FunctionUtils;

@Service
public class SaveStopRegRecordBusiness<M extends StopRegRecordDocumentContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    SaveStopRegRecordFunction func;
    @Override
    public void process(M ctx) throws Exception {
        FunctionUtils.invoke(func, ctx.getStopRegRecordDocument());
    }

}
