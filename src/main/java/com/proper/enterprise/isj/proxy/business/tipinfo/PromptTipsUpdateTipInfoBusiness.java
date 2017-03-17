package com.proper.enterprise.isj.proxy.business.tipinfo;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.TipInfoEntityContext;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.service.PromptTipsService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class PromptTipsUpdateTipInfoBusiness<M extends TipInfoEntityContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {
    @Autowired
    PromptTipsService tipService;

    @Override
    public void process(M ctx) throws Exception {
        BaseInfoEntity tipInfo = ctx.getTipInfoEntity();
        String retValue = "";
        if (StringUtil.isNotNull(tipInfo.getId())) {
            try {
                tipService.saveTipInfo(tipInfo);
            } catch (Exception e) {
                debug("PromptTipsController.updateTipInfo[Exception]:", e);
                throw new RuntimeException(APP_SYSTEM_ERR);
            }
        }
        ctx.setResult(retValue);
    }

}
