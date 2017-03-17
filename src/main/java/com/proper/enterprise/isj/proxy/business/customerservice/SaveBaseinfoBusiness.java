package com.proper.enterprise.isj.proxy.business.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BaseInfoEntityContext;
import com.proper.enterprise.isj.function.customservice.SaveBaseinfoFunction;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class SaveBaseinfoBusiness<M extends BaseInfoEntityContext<BaseInfoEntity> & ModifiedResultBusinessContext<BaseInfoEntity>>
        implements IBusiness<BaseInfoEntity, M> {

    @Autowired
    SaveBaseinfoFunction saveBaseinfoFunction;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(saveBaseinfoFunction.execute(ctx.getBasicInfoEntity()));
    }

}
