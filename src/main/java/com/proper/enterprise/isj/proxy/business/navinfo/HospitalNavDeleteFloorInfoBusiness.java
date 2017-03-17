package com.proper.enterprise.isj.proxy.business.navinfo;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.IdsContext;
import com.proper.enterprise.isj.proxy.service.HospitalNavigationService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HospitalNavDeleteFloorInfoBusiness<M extends IdsContext<Boolean> & ModifiedResultBusinessContext<Boolean>>
        implements IBusiness<Boolean, M>, ILoggable {

    @Autowired
    HospitalNavigationService navService;

    @Override
    public void process(M ctx) throws Exception {
        String ids = ctx.getIds();
        boolean retValue = false;
        if (StringUtil.isNotNull(ids)) {
            String[] idArr = ids.split(",");
            List<String> idList = new ArrayList<>();
            Collections.addAll(idList, idArr);
            try {
                navService.deleteWebNavInfo(idList);
                retValue = true;
            } catch (Exception e) {
                LOGGER.debug("HospitalNavigationController.deleteFloorInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR, e);
            }
        }
        ctx.setResult(retValue);
    }
}
