package com.proper.enterprise.isj.proxy.business.customerservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class UserInfoModifyFamilyMemberBusiness<M extends MapParamsContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {
    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoServiceImpl;

    @Override
    public void process(M ctx) throws Exception {
        Map<String, String> userMap = ctx.getParams();
        String id = userMap.get("id");
        String name = userMap.get("name");
        String phone = userMap.get("phone");
        String patientVisits = userMap.get("patientVisits");
        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(name) || StringUtil.isEmpty(phone)
                || StringUtil.isEmpty(patientVisits)) {
            ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.FORM_DATA_ERR, HttpStatus.BAD_REQUEST));
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            ctx.setResult(new ResponseEntity<>(CenterFunctionUtils.LOGIN_INVALID_ERR, HttpStatus.BAD_REQUEST));
            return;
        }
        UserInfoDocument userInfo = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        List<FamilyMemberInfoDocument> familyList = userInfo.getFamilyMemberInfo();
        if (familyList == null) {
            familyList = new ArrayList<>();
        }
        // boolean clearMedicalNumFlag = true;
        if (userInfo.getId().equals(id)) {
            /*
             * if (userInfo.getName().equals(name)&&userInfo.getPhone().equals(phone)) {
             * clearMedicalNumFlag = false;
             * }
             */
            userInfo.setName(name);
            userInfo.setPhone(phone);
            userInfo.setPatientVisits(patientVisits);
            if (patientVisits.equals(String.valueOf(1))) {
                for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                    familyMemberInfoDocument.setPatientVisits(String.valueOf(0));
                }
            }
            // if (clearMedicalNumFlag && StringUtil.isNotEmpty(userInfo.getMedicalNum())) {
            // Map<String, String> hisMedMap = userInfo.getMedicalNumMap();
            // hisMedMap.put(userInfo.getMedicalNum(), DateUtil.toTimestamp(new Date()));
            // userInfo.setMedicalNumMap(hisMedMap);
            // userInfo.setMedicalNum("");
            // }
        } else {
            for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                if (familyMemberInfoDocument.getId().equals(id)) {
                    /*
                     * if (familyMemberInfoDocument.getName().equals(name)&&familyMemberInfoDocument.getPhone().equals(
                     * phone)) {
                     * clearMedicalNumFlag = false;
                     * }
                     */
                    familyMemberInfoDocument.setPhone(phone);
                    familyMemberInfoDocument.setName(name);
                    familyMemberInfoDocument.setPatientVisits(patientVisits);
                    // if (clearMedicalNumFlag && StringUtil.isNotEmpty(familyMemberInfoDocument.getMedicalNum())) {
                    // Map<String, String> hisMedMap = familyMemberInfoDocument.getMedicalNumMap();
                    // hisMedMap.put(familyMemberInfoDocument.getMedicalNum(), DateUtil.toTimestamp(new Date()));
                    // familyMemberInfoDocument.setMedicalNumMap(hisMedMap);
                    // familyMemberInfoDocument.setMedicalNum("");
                    // }
                } else {
                    if (patientVisits.equals(String.valueOf(1))) {
                        familyMemberInfoDocument.setPatientVisits(String.valueOf(0));
                    }
                }
            }
            if (patientVisits.equals(String.valueOf(1))) {
                userInfo.setPatientVisits(String.valueOf(0));
            }
        }
        userInfo.setFamilyMemberInfo(familyList);
        userInfoServiceImpl.saveOrUpdateUserInfo(userInfo);
        ctx.setResult(new ResponseEntity<>("", HttpStatus.OK));
    }
}
