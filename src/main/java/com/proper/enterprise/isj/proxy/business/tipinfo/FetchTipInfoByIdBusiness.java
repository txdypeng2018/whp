package com.proper.enterprise.isj.proxy.business.tipinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.TipInfoIdContext;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class FetchTipInfoByIdBusiness<M extends TipInfoIdContext<BaseInfoEntity> & ModifiedResultBusinessContext<BaseInfoEntity>>
        implements IBusiness<BaseInfoEntity, M> {

    @Autowired
    BaseInfoRepository baseInfoRepo;

    @Override
    public void process(M ctx) throws Exception {
        ctx.setResult(baseInfoRepo.findById(ctx.getTipInfoId()));
    }

}