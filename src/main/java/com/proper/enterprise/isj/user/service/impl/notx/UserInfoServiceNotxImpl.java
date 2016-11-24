package com.proper.enterprise.isj.user.service.impl.notx;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.service.impl.UserInfoServiceImpl;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.IDCardType;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.enmus.Sex;
import com.proper.enterprise.isj.webservices.model.req.PatReq;
import com.proper.enterprise.isj.webservices.model.res.PatRes;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;
import com.proper.enterprise.platform.auth.jwt.model.JWTHeader;
import com.proper.enterprise.platform.auth.jwt.model.impl.JWTPayloadImpl;
import com.proper.enterprise.platform.auth.jwt.service.JWTService;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/9/16 0016.
 */
@Service
public class UserInfoServiceNotxImpl implements UserInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoServiceNotxImpl.class);

    @Autowired
    UserInfoServiceImpl userInfoServiceImpl;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    public WebServicesClient webServicesClient;

    @Override
    public UserInfoDocument saveOrUpdateUserInfo(UserInfoDocument userInfo) {
        userInfo = userInfoRepository.save(userInfo);
        return userInfo;
    }

    @Override
    public UserInfoDocument getUserInfoByUserId(String userId) {
        UserInfoDocument userInfo = userInfoRepository.getByUserId(userId);
        return userInfo;
    }

    // @Override
    // public UserInfoDocument getUserInfoByPhone(String telephone) {
    // UserInfoDocument userInfo = userInfoRepository.getByPhone(telephone);
    // return userInfo;
    // }

    @Override
    public UserInfoDocument saveUserAndUserInfo(UserEntity user, UserInfoDocument userInfo) throws Exception {
        try {
            userInfo = userInfoRepository.save(userInfo);
            userInfo = userInfoServiceImpl.saveUserAndUserInfo(user, userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            if (StringUtil.isNotEmpty(userInfo.getUserId())) {
                User us = userService.get(userInfo.getUserId());
                if (us == null) {
                    userInfoRepository.delete(userInfo);
                }
            }
            throw e;
        }

        return userInfo;
    }

    @Override
    public String userLogin(User user) throws Exception {
        JWTHeader header = new JWTHeader();
        header.setId(user.getId());
        header.setName(user.getUsername());
        JWTPayloadImpl payload = new JWTPayloadImpl();
        jwtService.clearToken(header);
        String token = jwtService.generateToken(header, payload);
        return token;
    }

    @Override
    public BasicInfoDocument getFamilyMemberByUserIdAndMemberId(String userId, String memberId) {
        UserInfoDocument userInfo = this.getUserInfoByUserId(userId);
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

    @Override
    public BasicInfoDocument getDefaultPatientVisitsUserInfo(String userId) {
        UserInfoDocument userInfo = this.getUserInfoByUserId(userId);
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

    @Override
    public UserInfoDocument saveOrUpdatePatientMedicalNum(String userId, String memberId, String medicalNum) throws Exception {
        BasicInfoDocument basicInfo = this.getFamilyMemberByUserIdAndMemberId(userId, memberId);
        ResModel<PatRes> patRes = checkPatientMedicalNumAndReturn(basicInfo, medicalNum);
        if (patRes.getReturnCode() == ReturnCode.ERROR) {
            LOGGER.debug(patRes.getReturnMsg());
            throw new HisReturnException("就诊人信息与医院预留信息不匹配,请核对就诊人信息或者到医院修改预留信息");
        }
        String returnMedicalNum = patRes.getRes().getCardNo();
        if (StringUtil.isEmpty(returnMedicalNum)) {
            LOGGER.debug("调用createPat返回的病历号为空,返回数据异常,病历号不应为空");
            throw new HisReturnException("绑定病历号失败");
        }
        UserInfoDocument userInfo = this.getUserInfoByUserId(userId);
        if (userInfo.getId().equals(memberId)) {
            userInfo.setMedicalNum(patRes.getRes().getCardNo());
        } else {
            List<FamilyMemberInfoDocument> familyMemberInfoDocumentList = userInfo.getFamilyMemberInfo();
            for (FamilyMemberInfoDocument familyMemberInfoDocument : familyMemberInfoDocumentList) {
                if (familyMemberInfoDocument.getId().equals(memberId)) {
                    familyMemberInfoDocument.setMedicalNum(patRes.getRes().getCardNo());
                }
            }
        }
       return  this.saveOrUpdateUserInfo(userInfo);
    }

    public ResModel<PatRes> checkPatientMedicalNumAndReturn(BasicInfoDocument basicInfo, String medicalNum)
            throws Exception {
        PatReq req = new PatReq();
        req.setHosId(CenterFunctionUtils.getHosId());
        req.setCardType(IDCardType.IDCARD);
        req.setMarkNo(basicInfo.getIdCard());
        if (StringUtil.isEmpty(medicalNum)) {
            req.setCardNo("");
        } else {
            req.setCardNo(medicalNum);
        }
        req.setAddress("");
        req.setName(basicInfo.getName());
        String sex = IdcardUtils.getGenderByIdCard(basicInfo.getIdCard());
        if (sex.equals("F")) {
            req.setSex(Sex.FEMALE);
        } else if (sex.equals("M")) {
            req.setSex(Sex.MALE);
        } else {
            req.setSex(Sex.SECRET);
        }
        req.setMobile(basicInfo.getPhone());
        req.setBirthday(DateUtil
                .toDateString(DateUtil.toDate(IdcardUtils.getBirthByIdCard(basicInfo.getIdCard()), "yyyyMMdd")));
        req.setAddress("");
        return webServicesClient.createPat(req);
    }
}