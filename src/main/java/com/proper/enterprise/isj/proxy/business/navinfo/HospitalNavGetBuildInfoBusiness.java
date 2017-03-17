package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BuildingNameContext;
import com.proper.enterprise.isj.context.DistrictCodeContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.PageSizeContext;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildEntity;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class HospitalNavGetBuildInfoBusiness<T, M extends DistrictCodeContext<NavigationBuildEntity> & BuildingNameContext<NavigationBuildEntity> & PageSizeContext<NavigationBuildEntity> & PageNoContext<NavigationBuildEntity> & ModifiedResultBusinessContext<NavigationBuildEntity>>
        implements IBusiness<NavigationBuildEntity, M> {
    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        // 取得Web端各院区楼列表
        NavigationBuildEntity buildInfo = navService.getWebBuildInfo(ctx.getDistrictCode(), ctx.getBuildingName(),
                String.valueOf(ctx.getPageNo()), String.valueOf(ctx.getPageSize()));
        ctx.setResult(buildInfo);
    }
}
