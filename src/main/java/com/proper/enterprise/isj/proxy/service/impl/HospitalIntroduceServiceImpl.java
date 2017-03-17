package com.proper.enterprise.isj.proxy.service.impl;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.business.his.HisFetchHospitalInfoFromHisBusiness;
import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.isj.webservices.model.res.HosInfo;

/**
 * 医院简介服务实现.
 * Created by think on 2016/8/29 0029.
 */
@Service
public class HospitalIntroduceServiceImpl extends AbstractService implements HospitalIntroduceService {
    
    @Override
    public HosInfo getHospitalInfoFromHis() throws Exception {
        return toolkit.execute(HisFetchHospitalInfoFromHisBusiness.class, c->{});
    }
}
