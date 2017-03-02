package com.proper.enterprise.isj.user.service;

import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;

import java.util.List;

/**
 * 用户扩展信息接口.
 * Created by think on 2016/8/15 0015.
 */
public interface UserInfoService {

    /**
     * 保存信息.
     */
    UserInfoDocument saveOrUpdateUserInfo(UserInfoDocument userInfo);

    /**
     * 根据用户Id查询信息.
     */
    UserInfoDocument getUserInfoByUserId(String userId);

//    UserInfoDocument getUserInfoByPhone(String telephone);

    UserInfoDocument saveUserAndUserInfo(UserEntity user, UserInfoDocument userInfo) throws Exception;

    /**
     * 根据用户Id查询信息.
     */
    BasicInfoDocument getFamilyMemberByUserIdAndMemberId(String userId, String memberId);

    /**
     *获取默认就诊人.
     */
    BasicInfoDocument getDefaultPatientVisitsUserInfo(String userId);

    /**
     * 在线建档病历号.
     * @param userId 用户编号.
     * @param memberId 患者编号.
     */
    UserInfoDocument saveOrUpdatePatientMedicalNum(String userId, String memberId, String medicalNum) throws Exception;

    /**
     * 获取家庭成员添加限制天数
     */
    int getFamilyAddLeftIntervalDays(int familyMemberSize, String lastCreateTime);

    /**
     * 通过姓名、卡号、手机号或身份证号取得用户信息
     */
    List<UserInfoDocument> getUserInfoList(String name, String medicalNum, String phone, String idCard);

    /**
     * 通过姓名、卡号、手机号或身份证号取得家庭成员信息
     */
    List<UserInfoDocument> getFamilyUserInfoList(String name, String medicalNum, String phone, String idCard);
}
