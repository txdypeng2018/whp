package com.proper.enterprise.isj.proxy.business.registration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.ConfCenter;

@Service
public class RegisterGetAgreementBusiness<M extends ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M> {
    @Autowired
    BaseInfoRepository baseRepo;

    @Override
    public void process(M ctx) throws Exception {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.agreement"));
        String guideMsg = infoList.get(0).getInfo();
        ctx.setResult(guideMsg);
    }
}