package com.proper.enterprise.isj.function.customservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.ConfCenter;

@Service
public class FetchServicePhoneNumFunction implements IFunction<String> {

    @Autowired
    BaseInfoRepository baseRepo;

    @Override
    public String execute(Object... params) throws Exception {
        return getPhoneNum();
    }
    
    public String getPhoneNum() {
        String phoneType = ConfCenter.get("isj.info.phone");
        List<BaseInfoEntity> retDoc = baseRepo.findByInfoType(phoneType);
        return retDoc.get(0).getInfo();
    }


}
