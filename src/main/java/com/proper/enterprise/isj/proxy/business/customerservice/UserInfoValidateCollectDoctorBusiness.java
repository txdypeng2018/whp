package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class UserInfoValidateCollectDoctorBusiness<M extends DoctorIdContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {
    @Autowired
    UserInfoService userInfoServiceImpl;

    @Autowired
    UserService userService;

    @Override
    public void process(M ctx) throws Exception {
        String doctorId = ctx.getDoctorId();
        User user = userService.getCurrentUser();
        UserInfoDocument info = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        String res = "";
        if (info.getDoctors() != null) {
            for (String docId : info.getDoctors()) {
                if (doctorId.equals(docId)) {
                    res = doctorId;
                }
            }
        }
        Map<String, String> docMap = new HashMap<>();
        docMap.put("doctorId", res);
        ctx.setResult(docMap);
    }
}
