package com.proper.enterprise.isj.user.service;

import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;

/**
 * Created by think on 2016/8/15 0015. 用户扩展信息接口
 */
public interface UserInfoService {

    /**
     * 保存信息
     */
    UserInfoDocument saveOrUpdateUserInfo(UserInfoDocument userInfo);

    /**
     * 根据用户Id查询信息
     */
    UserInfoDocument getUserInfoByUserId(String userId);

//    UserInfoDocument getUserInfoByPhone(String telephone);

    UserInfoDocument saveUserAndUserInfo(UserEntity user, UserInfoDocument userInfo) throws Exception;

    /**
     * 用户登录
     * 
     * @param user
     *            用户
     * @return 成功后产生的token
     * @throws Exception
     */
    String userLogin(User user) throws Exception;

    /**
     * 根据用户Id查询信息
     */
    BasicInfoDocument getFamilyMemberByUserIdAndMemberId(String userId, String memberId);

    /**
     *获取默认就诊人
     */
    BasicInfoDocument getDefaultPatientVisitsUserInfo(String userId);

    /**
     * 在线建档病历号
     * @param userId
     * @param memberId
     */
    UserInfoDocument saveOrUpdatePatientMedicalNum(String userId, String memberId, String medicalNum) throws Exception;

}
