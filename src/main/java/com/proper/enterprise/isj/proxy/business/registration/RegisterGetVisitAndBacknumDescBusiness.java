package com.proper.enterprise.isj.proxy.business.registration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.ConfCenter;

@Service
public class RegisterGetVisitAndBacknumDescBusiness<M extends ModifiedResultBusinessContext<Map<String, String>>>
        implements IBusiness<Map<String, String>, M> {
    @Autowired
    BaseInfoRepository baseRepo;

    @Override
    public void process(M ctx) throws Exception {
        List<BaseInfoEntity> visitInfo1 = baseRepo.findByInfoType(ConfCenter.get("isj.info.visit1"));
        List<BaseInfoEntity> visitInfo2 = baseRepo.findByInfoType(ConfCenter.get("isj.info.visit2"));
        List<BaseInfoEntity> backnumInfo = baseRepo.findByInfoType(ConfCenter.get("isj.info.backnum"));
        Map<String, String> retMap = new HashMap<>();
        retMap.put("visit1", visitInfo1.get(0).getInfo());
        retMap.put("visit2", visitInfo2.get(0).getInfo());
        retMap.put("backnum", backnumInfo.get(0).getInfo());
        ctx.setResult(retMap);
    }
}
