package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.api.IBusiness;

@Service
public class UserInfoDeleteCollectDoctorBusiness<M extends DoctorIdContext<String>> implements IBusiness<String, M> {
    @Autowired
    UserInfoService userInfoServiceImpl;

    @Autowired
    UserService userService;

    @Override
    public void process(M ctx) throws Exception {
        String doctorId = ctx.getDoctorId();
        User user = userService.getCurrentUser();
        UserInfoDocument info = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        List<String> docList = info.getDoctors();
        if (docList != null) {
            List<String> newList = new ArrayList<>();
            for (String s : docList) {
                if (!s.equals(doctorId)) {
                    newList.add(s);
                }
            }
            info.setDoctors(newList);
            userInfoServiceImpl.saveOrUpdateUserInfo(info);
        }
    }
}
