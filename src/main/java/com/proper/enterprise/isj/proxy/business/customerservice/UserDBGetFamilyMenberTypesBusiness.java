package com.proper.enterprise.isj.proxy.business.customerservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;

@Service
public class UserDBGetFamilyMenberTypesBusiness<M extends ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {
    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @Override
    public void process(M ctx) throws Exception {
        User user = userService.getCurrentUser();
        String sexCode = "";
        if (user != null) {
            UserInfoDocument userInfo = userInfoService.getUserInfoByUserId(user.getId());
            if (userInfo != null) {
                sexCode = userInfo.getSexCode();
            }
        }
        ctx.setResult(CenterFunctionUtils.getFamilyMenberTypeMap(sexCode));
    }
}
