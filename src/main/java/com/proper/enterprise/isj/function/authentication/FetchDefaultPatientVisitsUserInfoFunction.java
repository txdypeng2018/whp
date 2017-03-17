package com.proper.enterprise.isj.function.authentication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class FetchDefaultPatientVisitsUserInfoFunction implements IFunction<BasicInfoDocument> {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkitx;

    @Override
    public BasicInfoDocument execute(Object... params) throws Exception {
        return getDefaultPatientVisitsUserInfo((String) params[0]);
    }

    public BasicInfoDocument getDefaultPatientVisitsUserInfo(String userId) {
        UserInfoDocument userInfo = toolkitx.executeRepositoryFunction(UserInfoRepository.class, "getByUserId", userId);
        if (userInfo != null) {
            if (!userInfo.getPatientVisits().equals(String.valueOf(1))) {
                List<FamilyMemberInfoDocument> familyList = userInfo.getFamilyMemberInfo();
                for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                    if (familyMemberInfoDocument.getPatientVisits().equals(String.valueOf(1))) {
                        return familyMemberInfoDocument;
                    }
                }
            }
        }
        return userInfo;
    }

}
