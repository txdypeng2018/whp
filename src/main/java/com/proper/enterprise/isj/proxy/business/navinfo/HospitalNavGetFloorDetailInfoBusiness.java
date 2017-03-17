package com.proper.enterprise.isj.proxy.business.navinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FloorIdContext;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HospitalNavGetFloorDetailInfoBusiness<M extends FloorIdContext<NavigationFloorDetailEntity> & ModifiedResultBusinessContext<NavigationFloorDetailEntity>>
        implements IBusiness<NavigationFloorDetailEntity, M> {
    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        String floorId = ctx.getFloorId();
        NavigationFloorDetailEntity floorInfo = new NavigationFloorDetailEntity();
        if (StringUtil.isNotEmpty(floorId)) {
            floorInfo = navService.getWebFloorById(floorId);
        }
        ctx.setResult(floorInfo);
    }
}
