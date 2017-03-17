package com.proper.enterprise.isj.function.authentication;

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
public class FetchFamilyMemberByUserIdAndMemberIdFunction implements IFunction<BasicInfoDocument> {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkitx;

    @Override
    public BasicInfoDocument execute(Object... params) throws Exception {
        return getFamilyMemberByUserIdAndMemberId((String) params[0], (String) params[1]);
    }

    public BasicInfoDocument getFamilyMemberByUserIdAndMemberId(String userId, String memberId) {
        UserInfoDocument userInfo = toolkitx.executeRepositoryFunction(UserInfoRepository.class, "getByUserId", userId);
        if (userInfo != null) {
            if (!userId.equals(memberId)) {
                for (FamilyMemberInfoDocument familyMemberInfoDocument : userInfo.getFamilyMemberInfo()) {
                    if (familyMemberInfoDocument.getId().equals(memberId)) {
                        return familyMemberInfoDocument;
                    }
                }
            }
        }

        return userInfo;
    }

}
