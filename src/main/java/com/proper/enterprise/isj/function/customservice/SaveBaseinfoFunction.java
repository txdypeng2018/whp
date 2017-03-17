package com.proper.enterprise.isj.function.customservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class SaveBaseinfoFunction implements IFunction<BaseInfoEntity> {

    @Autowired
    BaseInfoRepository baseRepo;

    @Override
    public BaseInfoEntity execute(Object... params) throws Exception {
        saveBaseinfo((BaseInfoEntity) params[0]);
        return null;
    }

    public BaseInfoEntity saveBaseinfo(BaseInfoEntity baseInfo) {
        return baseRepo.save(baseInfo);
    }

}
