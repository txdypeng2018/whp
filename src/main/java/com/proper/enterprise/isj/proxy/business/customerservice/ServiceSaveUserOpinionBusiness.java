package com.proper.enterprise.isj.proxy.business.customerservice;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.ServiceUserOpinionDocumentContext;
import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.isj.proxy.enums.FeedbackEnum;
import com.proper.enterprise.isj.proxy.service.ServiceService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class ServiceSaveUserOpinionBusiness<M extends ServiceUserOpinionDocumentContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M>, ILoggable {
    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ServiceService serviceService;

    @Override
    public void process(M ctx) throws Exception {

        ServiceUserOpinionDocument opinionDocment = ctx.getOpinionDocment();
        String retValue = "";
        try {
            User currentUser = userService.getCurrentUser();
            BasicInfoDocument basicInfo = userInfoService.getUserInfoByUserId(currentUser.getId());
            opinionDocment.setUserId(currentUser.getId());
            opinionDocment.setUserName(basicInfo.getName());
            opinionDocment.setUserTel(basicInfo.getPhone());
            opinionDocment.setOpinionTime(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
            opinionDocment.setOpinion(opinionDocment.getOpinion());
            opinionDocment.setStatus(CenterFunctionUtils.UNREPLAY);
            opinionDocment.setStatusCode(FeedbackEnum.UNREPLAY.getValue());
            serviceService.saveOpinion(opinionDocment);
        } catch (Exception e) {
            debug("ServiceController.saveUserOpinion[Exception]:", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(retValue);
    }
}
