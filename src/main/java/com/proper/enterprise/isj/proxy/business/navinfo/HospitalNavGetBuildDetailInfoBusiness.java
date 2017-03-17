package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BuildingIdContext;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HospitalNavGetBuildDetailInfoBusiness<M extends BuildingIdContext<NavigationBuildDetailEntity> & ModifiedResultBusinessContext<NavigationBuildDetailEntity>>
        implements IBusiness<NavigationBuildDetailEntity, M> {
    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        String buildingId = ctx.getBuildingId();
        NavigationBuildDetailEntity buildInfo = new NavigationBuildDetailEntity();
        if (StringUtil.isNotEmpty(buildingId)) {
            buildInfo = navService.getWebBuildById(buildingId);
        }
        ctx.setResult(buildInfo);
    }
}
