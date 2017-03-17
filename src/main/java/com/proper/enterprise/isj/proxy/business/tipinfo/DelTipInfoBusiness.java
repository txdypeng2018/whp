package com.proper.enterprise.isj.proxy.business.tipinfo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.TipInfoIdsContext;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class DelTipInfoBusiness<M extends TipInfoIdsContext<Object>> implements IBusiness<Object, M> {

    @Autowired
    BaseInfoRepository baseInfoRepo;

    @Override
    public void process(M ctx) {
        List<BaseInfoEntity> baseInfoList = baseInfoRepo.findAll(ctx.getTipInfoIds());
        baseInfoRepo.delete(baseInfoList);
    }

}