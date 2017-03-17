package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MemberIdContext;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class UserInfoDeleteFamilyMemberBusiness<M extends MemberIdContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {

    @Autowired
    UserInfoService userInfoServiceImpl;

    @Autowired
    UserService userService;

    @Override
    public void process(M ctx) {
        String memberId = ctx.getMemberId();
        User user = userService.getCurrentUser();
        if (user == null) {
            ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.LOGIN_INVALID_ERR, HttpStatus.BAD_REQUEST));
            return;
        }
        UserInfoDocument userInfo = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        List<FamilyMemberInfoDocument> familyList = userInfo.getFamilyMemberInfo();
        if (familyList != null && familyList.size() > 0) {
            for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                if (familyMemberInfoDocument.getId().equals(memberId)) {
                    familyMemberInfoDocument.setId(UUID.randomUUID().toString().substring(0, 32));
                    familyMemberInfoDocument.setDeleteStatus(String.valueOf(1));
                    if (familyMemberInfoDocument.getPatientVisits().equals(String.valueOf(1))) {
                        userInfo.setPatientVisits(String.valueOf(1));
                    }
                }
            }
            userInfoServiceImpl.saveOrUpdateUserInfo(userInfo);
        }
        ctx.setResult(new ResponseEntity<>("", HttpStatus.OK));
    }
}
