package com.proper.enterprise.isj.user.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import com.proper.enterprise.isj.payment.logger.LoggerTestAdvice;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.isj.user.service.UserInfoPublicServiceTest;
import com.proper.enterprise.isj.user.service.impl.notx.UserInfoServiceNotxImpl;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.auth.jwt.service.JWTAuthcService;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.test.AbstractTest;

/**
 * Created by think on 2016/8/15 0015.
 */

public class UserInfoControllerTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoControllerTest.class);

    @Autowired
    JWTAuthcService jwtAuthcService;

    @Autowired
    UserInfoPublicServiceTest userInfoPublicServiceTest;

    @Autowired
    UserInfoServiceNotxImpl userInfoServiceNotx;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Test
    public void testGetUserInfoByToken() {
        try {
            User user = userInfoPublicServiceTest.saveUser();
            String token = jwtAuthcService.getUserToken(user.getUsername());
            mockRequest.addHeader("Authorization", token);
            MvcResult result = get("/user/userInfo", HttpStatus.OK);
            LOGGER.debug(result.getResponse().getContentAsString());
        } catch (Exception e) {
            LOGGER.debug("UserInfoControllerTest.testGetUserInfoByToken[Exception]:", e);
        }
    }

    @Test
    public void testAddMedicalNum() throws Exception {
        /*-----------新注册用户-------------*/
        UserInfoDocument userInfo = this.saveTestUserInfo();
        LoggerTestAdvice.setAsUserInfoWebServiceClientCustom(true);
        userInfoServiceNotx.saveOrUpdatePatientMedicalNum(userInfo.getUserId(), userInfo.getId(), null);
        /*----------------添加家庭成员(身份证号为错误)-------------*/
        boolean errFlag = false;
        try {
            userInfo = this.saveErrorFamilyMember(userInfo.getId());
            userInfoServiceNotx.saveOrUpdatePatientMedicalNum(userInfo.getUserId(),
                    userInfo.getFamilyMemberInfo().get(0).getId(), null);
        } catch (Exception e) {
            LOGGER.debug("------------抛异常正常----------");
            errFlag = true;
        }
        if (!errFlag) {
            throw new Exception();
        }
        /*----------------修改家庭成员的身份证号(改为正确的)--------------*/
        userInfo = this.updateCurrentFamilyMember(userInfo.getId());
        userInfoServiceNotx.saveOrUpdatePatientMedicalNum(userInfo.getUserId(),
                userInfo.getFamilyMemberInfo().get(0).getId(), null);
        /*--------------------将用户的病历号改为错误的病历号进行更新---------------*/
        errFlag = false;
        try {
            userInfo = this.updateErrorUserMedicalNum(userInfo.getId());
            userInfoServiceNotx.saveOrUpdatePatientMedicalNum(userInfo.getUserId(), userInfo.getId(),
                    userInfo.getMedicalNum());
        } catch (Exception e) {
            LOGGER.debug("------------抛异常正常----------");
            errFlag = true;
        }
        if (!errFlag) {
            throw new Exception();
        }
        /*--------------------将用户的病历号改为没有的病历号进行更新---------------*/
        errFlag = false;
        try {
            userInfo = this.updateNonExistUserMedicalNum(userInfo.getId());
            userInfoServiceNotx.saveOrUpdatePatientMedicalNum(userInfo.getUserId(), userInfo.getId(),
                    userInfo.getMedicalNum());
        } catch (Exception e) {
            LOGGER.debug("------------抛异常正常----------");
            errFlag = true;
        }
        if (!errFlag) {
            throw new Exception();
        }
        /*--------------------将用户的病历号改为自己另一个病历号进行更新---------------*/
        userInfo = this.updateCurrentUserMedicalNum(userInfo.getId());
        userInfoServiceNotx.saveOrUpdatePatientMedicalNum(userInfo.getUserId(), userInfo.getId(),
                userInfo.getMedicalNum());

        /*----------删除测试数据----------*/
        userInfoRepository.delete(userInfo.getId());
        LoggerTestAdvice.setAsUserInfoWebServiceClientCustom(false);
    }

    @Test
    @Sql
    public void testFamilyAddLimit() throws Exception {
        /*----------添加基础数据----------*/
        UserInfoDocument userInfo = this.saveTestUserInfo();
        userInfo = this.updateCurrentFamilyMember(userInfo.getId());
        mockUser(userInfo.getUserId(), "13800000001");
        /*----------验证方法----------*/
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("name", "啊啊啊");
        jsonMap.put("sexCode", "1");
        jsonMap.put("idCard", "210101198901015937");
        jsonMap.put("phone", "13800000001");
        jsonMap.put("memberCode", "10");
        jsonMap.put("member", "其他");
        jsonMap.put("patientVisits", "0");
        post("/user/familyMembers/familyMember", MediaType.TEXT_PLAIN, JSONUtil.toJSON(jsonMap),
                HttpStatus.BAD_REQUEST);
        /*----------删除测试数据----------*/
        userInfoRepository.delete(userInfo.getId());
    }

    /*----------------绑卡检验规则-----------*/
    /*
     * 1.在线建档 1)HIS端未查到患者病历号相关信息,将身份证和姓名做为绑卡条件,在HIS上生成病历号并返回
     * 2)如果根据身份证获得了病历号,但是病历号对应的记录中,姓名与传入不符,将返回错误消息 2.输入病历号,进行更新操作
     * 传入查询条件为:病历号+身份证号+姓名+手机号 HIS检验规则为:病历号+姓名,身份证号与手机号二选一,的方式进行比对
     * 如果查到符合校验规则的记录,将返回传入的病历号
     */
    /*----------------绑卡检验规则-----------*/
    /**
     * 新增用户进行在线建档 绑定成功
     */
    private UserInfoDocument saveTestUserInfo() {
        UserInfoDocument userInfo = new UserInfoDocument();
        userInfo.setUserId(DateUtil.toTimestamp(new Date(), true));
        userInfo.setName("210100199001010001");
        userInfo.setPhone("13800000001");
        userInfo.setIdCard("210100199001010001");
        return userInfoServiceNotx.saveOrUpdateUserInfo(userInfo);
    }

    /**
     * 添加家庭成员,将身份证号与用户身份证一样,进行绑卡操作,返回错误
     */
    private UserInfoDocument saveErrorFamilyMember(String userInfoId) {
        UserInfoDocument userInfo = userInfoRepository.findOne(userInfoId);
        List<FamilyMemberInfoDocument> fList = new ArrayList<>();
        FamilyMemberInfoDocument fm = new FamilyMemberInfoDocument();
        fm.setId(DateUtil.toTimestamp(new Date(), true));
        fm.setName("210100199001010002");
        fm.setPhone("13800000002");
        fm.setIdCard("210100199001010001");
        fList.add(fm);
        userInfo.setFamilyMemberInfo(fList);
        return userInfoServiceNotx.saveOrUpdateUserInfo(userInfo);
    }

    /**
     * 更新家庭成员,将身份证号改成家庭成员正确的身份证号,进行再次绑定
     */
    private UserInfoDocument updateCurrentFamilyMember(String userInfoId) {
        UserInfoDocument userInfo = userInfoRepository.findOne(userInfoId);
        List<FamilyMemberInfoDocument> fList = new ArrayList<>();
        FamilyMemberInfoDocument fm = new FamilyMemberInfoDocument();
        fm.setId(DateUtil.toTimestamp(new Date(), true));
        fm.setName("210100199001010002");
        fm.setPhone("13800000002");
        fm.setIdCard("210100199001010002");
        fList.add(fm);
        userInfo.setFamilyMemberInfo(fList);
        return userInfoServiceNotx.saveOrUpdateUserInfo(userInfo);
    }

    /**
     * 将用户的病历号改为家庭成员绑定的病历号,进行更新操作,返回错误
     */
    private UserInfoDocument updateErrorUserMedicalNum(String userInfoId) {
        UserInfoDocument userInfo = userInfoRepository.findOne(userInfoId);
        userInfo.setMedicalNum("210100199001010002");
        return userInfoServiceNotx.saveOrUpdateUserInfo(userInfo);
    }

    /**
     * 将用户的病历号改为根本查不到的病历号
     */
    private UserInfoDocument updateNonExistUserMedicalNum(String userInfoId) {
        UserInfoDocument userInfo = userInfoRepository.findOne(userInfoId);
        userInfo.setMedicalNum("M_NON");
        return userInfoServiceNotx.saveOrUpdateUserInfo(userInfo);
    }

    /**
     * 输入用户正确的病历号信息更新操作
     */
    private UserInfoDocument updateCurrentUserMedicalNum(String userInfoId) {
        UserInfoDocument userInfo = userInfoRepository.findOne(userInfoId);
        userInfo.setMedicalNum("13800000001");
        return userInfoServiceNotx.saveOrUpdateUserInfo(userInfo);
    }
}