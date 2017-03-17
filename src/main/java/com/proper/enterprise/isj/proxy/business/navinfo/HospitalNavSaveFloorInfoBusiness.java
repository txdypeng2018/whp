package com.proper.enterprise.isj.proxy.business.navinfo;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_PARAM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.FloorInfoEntityContext;
import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HospitalNavSaveFloorInfoBusiness<M extends FloorInfoEntityContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {

    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        NavigationFloorDetailEntity floorInfo = ctx.getFloorInfo();

        String retValue = "";
        try {
            if (StringUtil.isNotEmpty(floorInfo.getFloorParentId())) {
                navService.saveWebFloorInfo(floorInfo);
            } else {
                throw new RuntimeException(APP_PARAM_ERR);
            }
        } catch (Exception e) {
            debug("HospitalNavigationController.saveFloorInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(retValue);
    }

}
