package com.proper.enterprise.isj.proxy.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DoctorIdSetContext;
import com.proper.enterprise.isj.proxy.business.his.HisFetchOA2HISDoctorInfoBusiness;
import com.proper.enterprise.isj.proxy.service.DoctorService;
import com.proper.enterprise.isj.support.service.AbstractService;

/**
 * 医生信息服务实现.
 * Created by think on 2016/8/19 0019.
 */
@Service
public class DoctorServiceImpl extends AbstractService implements DoctorService {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Map<String, Object>> getOA2HISDoctorInfo(Set<String> doctorIdSet) {
        return toolkit.execute(HisFetchOA2HISDoctorInfoBusiness.class, ctx->((DoctorIdSetContext<Map<String, Map<String, Object>>>)ctx).setDoctorIds(doctorIdSet));
    }

}
