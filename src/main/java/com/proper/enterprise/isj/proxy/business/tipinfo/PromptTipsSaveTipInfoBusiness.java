package com.proper.enterprise.isj.proxy.business.tipinfo;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_INFOTYPE_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.TipInfoEntityContext;
import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.service.PromptTipsService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.service.UserService;

@Service
public class PromptTipsSaveTipInfoBusiness<M extends TipInfoEntityContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    PromptTipsService tipService;

    @Autowired
    BaseInfoRepository baseInfoRepo;

    @Override
    public void process(M ctx) throws Exception {
        BaseInfoEntity tipInfo = ctx.getTipInfoEntity();
        String retValue = "";
        try {
            String infoType = tipInfo.getInfoType();
            if (baseInfoRepo.findByInfoType(infoType).size() == 0) {
                tipService.saveTipInfo(tipInfo);
            } else {
                throw new RuntimeException(APP_INFOTYPE_ERR);
            }

        } catch (Exception e) {
            debug("PromptTipsController.saveTipInfo[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR);
        }
        ctx.setResult(retValue);

    }
}
