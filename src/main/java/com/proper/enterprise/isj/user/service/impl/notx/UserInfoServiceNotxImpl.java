package com.proper.enterprise.isj.user.service.impl.notx;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.authentication.CheckPatientMedicalNumAndReturnFunction;
import com.proper.enterprise.isj.function.authentication.FetchDefaultPatientVisitsUserInfoFunction;
import com.proper.enterprise.isj.function.authentication.FetchFamilyAddLeftIntervalDaysFunction;
import com.proper.enterprise.isj.function.authentication.FetchFamilyMemberByUserIdAndMemberIdFunction;
import com.proper.enterprise.isj.function.authentication.SaveOrUpdatePatientMedicalNumFunction;
import com.proper.enterprise.isj.function.authentication.SaveUserAndUserInfoFunction;
import com.proper.enterprise.isj.function.customservice.FetchFamilyUserInfoListFunction;
import com.proper.enterprise.isj.function.customservice.FetchUserInfoList;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.webservices.model.res.PatRes;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;

/**
 * 用户服务.
 * Created by think on 2016/9/16 0016.
 */
@Service
public class UserInfoServiceNotxImpl extends AbstractService implements UserInfoService {

    @Override
    public UserInfoDocument saveOrUpdateUserInfo(UserInfoDocument userInfo) {
        return toolkit.executeRepositoryFunction(UserInfoRepository.class, "save", userInfo);
    }

    @Override
    public UserInfoDocument getUserInfoByUserId(String userId) {
        return toolkit.executeRepositoryFunction(UserInfoRepository.class, "getByUserId", userId);
    }

    @Override
    public UserInfoDocument saveUserAndUserInfo(UserEntity user, UserInfoDocument userInfo) throws Exception {
        return toolkit.executeFunction(SaveUserAndUserInfoFunction.class, user, userInfo);
    }

    @Override
    public BasicInfoDocument getFamilyMemberByUserIdAndMemberId(String userId, String memberId) {
        return toolkit.executeFunction(FetchFamilyMemberByUserIdAndMemberIdFunction.class, userId, memberId);
    }

    @Override
    public BasicInfoDocument getDefaultPatientVisitsUserInfo(String userId) {
        return toolkit.executeFunction(FetchDefaultPatientVisitsUserInfoFunction.class, userId);
    }

    @Override
    public UserInfoDocument saveOrUpdatePatientMedicalNum(String userId, String memberId, String medicalNum) throws Exception {
        return toolkit.executeFunction(SaveOrUpdatePatientMedicalNumFunction.class, userId, memberId, medicalNum);

    }

    public ResModel<PatRes> checkPatientMedicalNumAndReturn(BasicInfoDocument basicInfo, String medicalNum)
            throws Exception {
        return toolkit.executeFunction(CheckPatientMedicalNumAndReturnFunction.class, basicInfo,
                medicalNum);
    }

    public int getFamilyAddLeftIntervalDays(int familyMemberSize, String lastCreateTime) {
        return toolkit.executeFunction(FetchFamilyAddLeftIntervalDaysFunction.class, familyMemberSize,
                lastCreateTime);
    }

    @Override
    public List<UserInfoDocument> getUserInfoList(String name, String medicalNum, String phone, String idCard) {
        return toolkit.executeFunction(FetchUserInfoList.class, name, medicalNum, phone, idCard);
    }

    @Override
    public List<UserInfoDocument> getFamilyUserInfoList(String name, String medicalNum, String phone, String idCard) {
        return toolkit.executeFunction(FetchFamilyUserInfoListFunction.class, name, medicalNum, phone, idCard);
    }
}