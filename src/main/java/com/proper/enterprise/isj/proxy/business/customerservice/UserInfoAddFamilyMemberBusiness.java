package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.proper.enterprise.isj.context.HttpServletRequestContext;
import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.model.enums.SexTypeEnum;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.user.utils.MobileNoUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.converter.AESStringConverter;
import com.proper.enterprise.platform.core.utils.StringUtil;

public class UserInfoAddFamilyMemberBusiness<T, M extends HttpServletRequestContext<Object> & MapParamsContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {

    @Autowired
    UserService userService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    UserInfoService userInfoServiceImpl;

    @Override
    public void process(M ctx) throws Exception {
        Map<String, String> userMap = ctx.getParams();

        String name = userMap.get("name");
        // String sex = userMap.get("sex");
        String sexCode = userMap.get("sexCode");
        String idCard = userMap.get("idCard");
        String phone = userMap.get("phone");
        String memberCode = userMap.get("memberCode");
        String member = userMap.get("member");
        String patientVisits = userMap.get("patientVisits");
        if (StringUtil.isEmpty(name) || StringUtil.isEmpty(sexCode) || StringUtil.isEmpty(idCard)
                || StringUtil.isEmpty(phone) || StringUtil.isEmpty(memberCode) || StringUtil.isEmpty(patientVisits)) {
            ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.FORM_DATA_ERR, HttpStatus.BAD_REQUEST));
        }
        idCard = idCard.toUpperCase();
        if (!IdcardUtils.validateCard(idCard)) {
            ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.IDCARD_ERROR, HttpStatus.BAD_REQUEST));
        }
        if (IdcardUtils.getGenderByIdCard(idCard).equals("M")) {
            if (!sexCode.equals(String.valueOf(SexTypeEnum.MALE.getCode()))) {
                ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.IDCARD_SEX_ERROR, HttpStatus.BAD_REQUEST));
            }

        } else if (IdcardUtils.getGenderByIdCard(idCard).equals("F")) {
            if (!sexCode.equals(String.valueOf(SexTypeEnum.FEMALE.getCode()))) {
                ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.IDCARD_SEX_ERROR, HttpStatus.BAD_REQUEST));
            }
        }

        if (!MobileNoUtils.isMobileNo(phone)) {
            ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.PHONE_ERROR, HttpStatus.BAD_REQUEST));
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.LOGIN_INVALID_ERR, HttpStatus.BAD_REQUEST));
            return;
        }

        UserInfoDocument userInfo = userInfoServiceImpl.getUserInfoByUserId(user.getId());

        List<FamilyMemberInfoDocument> familyList = userInfo.getFamilyMemberInfo();

        int familyMemberSize = 1;
        String lastCreateTime = userInfo.getCreateTime();
        if (familyList != null) {
            familyMemberSize += familyList.size();
            for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                if (StringUtil.isNotEmpty(familyMemberInfoDocument.getCreateTime())) {
                    if (StringUtil.isEmpty(lastCreateTime)
                            || familyMemberInfoDocument.getCreateTime().compareTo(lastCreateTime) > 0) {
                        lastCreateTime = familyMemberInfoDocument.getCreateTime();
                    }
                }
            }
        }
        if (StringUtil.isNotEmpty(lastCreateTime)) {
            int leftIntervalDays = userInfoServiceImpl.getFamilyAddLeftIntervalDays(familyMemberSize, lastCreateTime);
            if (leftIntervalDays > 0) {
                ctx.setResult(new ResponseEntity<String>("家庭成员添加次数过多，请" + leftIntervalDays + "天后再添加",
                        HttpStatus.BAD_REQUEST));
            }
        }

        FamilyMemberInfoDocument familyMemberInfoDocument = new FamilyMemberInfoDocument();
        familyMemberInfoDocument.setPhone(phone);
        familyMemberInfoDocument.setPatientVisits(patientVisits);
        familyMemberInfoDocument.setName(name);

        if (String.valueOf(SexTypeEnum.MALE.getCode()).equals(sexCode)) {
            familyMemberInfoDocument.setSex(CenterFunctionUtils.SEX_MALE);
        }
        if (String.valueOf(SexTypeEnum.FEMALE.getCode()).equals(sexCode)) {
            familyMemberInfoDocument.setSex(CenterFunctionUtils.SEX_FEMALE);
        }
        familyMemberInfoDocument.setSexCode(sexCode);
        familyMemberInfoDocument.setMemberCode(memberCode);
        familyMemberInfoDocument.setMember(member);
        familyMemberInfoDocument.setIdCard(idCard);
        List<RegistrationDocument> regList = registrationService.findRegistrationDocumentByCreateUserIdAndPatientIdCard(
                user.getId(), new AESStringConverter().convertToDatabaseColumn(idCard));
        if (regList.size() > 0) {
            familyMemberInfoDocument.setId(regList.get(0).getPatientId());
        } else {
            familyMemberInfoDocument.setId(UUID.randomUUID().toString().substring(0, 32));
        }

        if (familyList == null) {
            familyList = new ArrayList<>();
        }
        if (patientVisits.equals(String.valueOf(1))) {
            userInfo.setPatientVisits(String.valueOf(0));
            for (FamilyMemberInfoDocument memberInfoDocument : familyList) {
                memberInfoDocument.setPatientVisits(String.valueOf(0));
            }
        }
        boolean hasIdCard = false;
        if (userInfo.getIdCard().equals(idCard)) {
            hasIdCard = true;
        } else {
            for (FamilyMemberInfoDocument memberInfoDocument : familyList) {
                if (StringUtil.isEmpty(memberInfoDocument.getDeleteStatus())
                        && memberInfoDocument.getIdCard().equals(idCard)) {
                    hasIdCard = true;
                }

            }
        }
        if (hasIdCard) {
            ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.IDCARD_EXIST, HttpStatus.BAD_REQUEST));
        }
        familyList.add(familyMemberInfoDocument);
        userInfo.setFamilyMemberInfo(familyList);
        userInfoServiceImpl.saveOrUpdateUserInfo(userInfo);
        ctx.setResult(new ResponseEntity<>("", HttpStatus.CREATED));
    }
}
