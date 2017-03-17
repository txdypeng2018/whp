package com.proper.enterprise.isj.proxy.business.tipinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.TipInfoEntityContext;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class SaveTipInfoBusiness<M extends TipInfoEntityContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    BaseInfoRepository baseInfoRepo;

    @Override
    public void process(M ctx) throws Exception {
        baseInfoRepo.save(ctx.getTipInfoEntity());
    }

}