package com.proper.enterprise.isj.proxy.business.navinfo;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FloorInfoEntityContext;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class HospitalNavUpdateFloorInfoBusiness<M extends FloorInfoEntityContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {

    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        NavigationFloorDetailEntity floorInfo = ctx.getFloorInfo();
        String retValue = "";
        try {
            navService.updateWebFloorInfo(floorInfo);
        } catch (Exception e) {
            debug("HospitalNavigationController.updateFloorInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(retValue);
    }
}
