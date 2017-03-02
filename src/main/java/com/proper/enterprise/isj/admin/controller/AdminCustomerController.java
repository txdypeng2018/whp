package com.proper.enterprise.isj.admin.controller;

import com.proper.enterprise.isj.admin.service.AdminCustomerService;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/admin/customer")
public class AdminCustomerController extends BaseController {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    MessagesService messagesService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    AdminCustomerService adminCustomerService;

    /**
     * 取得人员信息列表
     */
    @GetMapping(path = "/users")
    public ResponseEntity<List<Map<String, Object>>> users(String name, String medicalNum, String phoneOrIdcard)
            throws Exception {
        String phone = "";
        String idCard = "";
        if (StringUtil.isNotEmpty(phoneOrIdcard)) {
            if (phoneOrIdcard.length() == 11) {
                phone = phoneOrIdcard;
            } else {
                idCard = phoneOrIdcard;
            }
        }
        List<UserInfoDocument> userInfoList = userInfoService.getUserInfoList(name, medicalNum, phone, idCard);
        List<UserInfoDocument> familyUserInfoList = userInfoService.getFamilyUserInfoList(name, medicalNum, phone, idCard);
        List<Map<String, Object>> allUserInfoList = new ArrayList<Map<String, Object>>();
        Map<String, Object> userInfo = null;
        for (UserInfoDocument document : userInfoList) {
            userInfo = adminCustomerService.setUserInfo(document.getUserId(), document, "1", null);
            allUserInfoList.add(userInfo);
        }
        for (UserInfoDocument document : familyUserInfoList) {
            if (document.getFamilyMemberInfo() != null && document.getFamilyMemberInfo().size() > 0) {
                for (FamilyMemberInfoDocument familyDocument : document.getFamilyMemberInfo()) {
                    if (name.equals(familyDocument.getName()) && medicalNum.equals(familyDocument.getMedicalNum())) {
                        userInfo = adminCustomerService.setUserInfo(familyDocument.getId(), familyDocument, "2", document);
                        allUserInfoList.add(userInfo);
                    }
                }
            }
        }
        return responseOfGet(allUserInfoList);
    }

    /**
     * 取得挂号信息列表
     */
    @GetMapping(path = "/registrations")
    public ResponseEntity<List<RegistrationDocument>> registrations(String userId) throws Exception {
        BasicInfoDocument basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(userId);
        List<RegistrationDocument> regList = null;
        if (basicInfo == null) {
            regList = registrationService.findRegistrationDocumentList(userId);
        } else {
            regList = registrationService.findRegistrationDocumentList(basicInfo.getId());
        }
        return responseOfGet(regList);
    }

    /**
     * 取得缴费信息列表
     */
    @GetMapping(path = "/recipeorders")
    public ResponseEntity<List<Map<String, Object>>> recipeorders(String userId) throws Exception {
        BasicInfoDocument basicInfo = userInfoService.getDefaultPatientVisitsUserInfo(userId);
        List<RecipeOrderDocument> recipeList = null;
        if (basicInfo == null) {
            recipeList = recipeService.findRecipeOrderDocumentList(userId);
        } else {
            recipeList = recipeService.findRecipeOrderDocumentList(basicInfo.getId());
        }
        List<Map<String, Object>> recipes = new ArrayList<Map<String, Object>>();
        Map<String, Object> recipeMap = null;
        for (RecipeOrderDocument recipe : recipeList) {
            recipeMap = adminCustomerService.setRecipeOrderInfo(recipe);
            recipes.add(recipeMap);
        }
        return responseOfGet(recipes);
    }

    /**
     * 取得消息信息列表
     */
    @GetMapping(path = "/messages")
    public ResponseEntity<List<MessagesDocument>> messages(String userId) throws Exception {
        List<MessagesDocument> msgList = messagesService.findMessagesDocumentList(userId);
        return responseOfGet(msgList);
    }
}
