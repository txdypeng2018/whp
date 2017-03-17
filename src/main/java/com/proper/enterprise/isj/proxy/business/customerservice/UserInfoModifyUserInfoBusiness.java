package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class UserInfoModifyUserInfoBusiness<M extends MapParamsContext<UserInfoDocument> & ModifiedResultBusinessContext<UserInfoDocument>>
        implements IBusiness<UserInfoDocument, M> {
    @Autowired
    UserInfoService userInfoServiceImpl;

    @Override
    public void process(M ctx) {
        Map<String, String> phoneMap = ctx.getParams();
        String userId = phoneMap.get("userId");
        String name = phoneMap.get("name");
        String phoneNo = phoneMap.get("phoneNo");
        UserInfoDocument info = null;
        if (StringUtil.isNotEmpty(userId) && StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(phoneNo)) {
            info = userInfoServiceImpl.getUserInfoByUserId(userId);
            if (info != null) {
                info.setName(name);
                info.setPhone(phoneNo);
                info = userInfoServiceImpl.saveOrUpdateUserInfo(info);
            }
        }
        ctx.setResult(info);
    }
}
