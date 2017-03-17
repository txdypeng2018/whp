package com.proper.enterprise.isj.proxy.business.navinfo;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_PARAM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.BuildInfoEntityContext;
import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HospitalNavUpdateBuildInfoBusiness<M extends BuildInfoEntityContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {

    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        NavigationBuildDetailEntity buildInfo = ctx.getBuildInfo();
        String retValue = "";
        try {
            if (StringUtil.isNotNull(buildInfo.getDistrictCode())) {
                navService.updateWebBuildInfo(buildInfo);
            } else {
                throw new RuntimeException(APP_PARAM_ERR);
            }
        } catch (Exception e) {
            debug("HospitalNavigationController.updateBuildInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(retValue);
    }

}
