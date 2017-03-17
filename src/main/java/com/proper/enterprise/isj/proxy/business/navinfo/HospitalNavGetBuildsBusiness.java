package com.proper.enterprise.isj.proxy.business.navinfo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class HospitalNavGetBuildsBusiness<M extends DistrictIdContext<List<Map<String, Object>>> & ModifiedResultBusinessContext<List<Map<String, Object>>>>
        implements IBusiness<List<Map<String, Object>>, M> {
    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        String districtId = ctx.getDistrictId();
        List<Map<String, Object>> retList = navService.getAppDistrictsList(districtId);
        ctx.setResult(retList);
    }

}
