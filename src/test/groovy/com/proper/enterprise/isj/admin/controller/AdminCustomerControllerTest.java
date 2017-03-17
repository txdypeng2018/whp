package com.proper.enterprise.isj.admin.controller;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.recipe.RecipeOrderDocument;
import com.proper.enterprise.isj.proxy.repository.MessagesRepository;
import com.proper.enterprise.isj.proxy.repository.RecipeOrderRepository;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.proxy.service.RecipeService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.test.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminCustomerControllerTest extends AbstractTest {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeOrderRepository recipeOrderRepository;

    @Autowired
    MessagesService messagesService;

    @Autowired
    MessagesRepository messagesRepository;

    @SuppressWarnings("unchecked")
    @Test
    public void testUsers() throws Exception {
        MvcResult result = get("/admin/customer/users?name=姓名&medicalNum=M000000000&phoneOrIdcard=13600000000", HttpStatus.OK);
        String resultContent = result.getResponse().getContentAsString();
        List<Map<String, Object>> list = (List<Map<String, Object>>)JSONUtil.parse(resultContent, Object.class);
        assert list.size() == 2;
        result = get("/admin/customer/users?name=姓名&medicalNum=M000000000&phoneOrIdcard=210106000000000000", HttpStatus.OK);
        resultContent = result.getResponse().getContentAsString();
        list = (List<Map<String, Object>>)JSONUtil.parse(resultContent, Object.class);
        assert list.size() == 2;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegistrations() throws Exception {
        MvcResult result = get("/admin/customer/registrations?userId=6eda5afd-6c5f-42c0-91de-653fb04f990b", HttpStatus.OK);
        String resultContent = result.getResponse().getContentAsString();
        List<RegistrationDocument> list = (List<RegistrationDocument>)JSONUtil.parse(resultContent, Object.class);
        assert list.size() == 1;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRecipeorders() throws Exception {
        MvcResult result = get("/admin/customer/recipeorders?userId=6eda5afd-6c5f-42c0-91de-653fb04f990b", HttpStatus.OK);
        String resultContent = result.getResponse().getContentAsString();
        List<Map<String, Object>> list = (List<Map<String, Object>>)JSONUtil.parse(resultContent, Object.class);
        assert list.size() == 1;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMessages() throws Exception {
        MvcResult result = get("/admin/customer/messages?userId=6eda5afd-6c5f-42c0-91de-653fb04f990b", HttpStatus.OK);
        String resultContent = result.getResponse().getContentAsString();
        List<MessagesDocument> list = (List<MessagesDocument>)JSONUtil.parse(resultContent, Object.class);
        assert list.size() == 1;
    }

    @After
    public void clearTestData() throws Exception {
        userInfoRepository.deleteAll();
        registrationRepository.deleteAll();
        recipeOrderRepository.deleteAll();
        messagesRepository.deleteAll();
    }

    @Before
    public void saveTestData() throws Exception {
        UserInfoDocument userInfoDocument = new UserInfoDocument();
        userInfoDocument.setId("58aaa76c5595753d58b78479");
        userInfoDocument.setUserId("6eda5afd-6c5f-42c0-91de-653fb04f990b");
        userInfoDocument.setName("姓名");
        userInfoDocument.setMedicalNum("M000000000");
        userInfoDocument.setPhone("13600000000");
        userInfoDocument.setIdCard("210106000000000000");
        userInfoService.saveOrUpdateUserInfo(userInfoDocument);
        userInfoDocument = new UserInfoDocument();
        userInfoDocument.setId("58aaa76c5595753d58b78471");
        userInfoDocument.setUserId("6eda5afd-6c5f-42c0-91de-2");
        userInfoDocument.setName("姓名1");
        userInfoDocument.setMedicalNum("M000000001");
        userInfoDocument.setPhone("13600000001");
        userInfoDocument.setIdCard("210106000000000001");
        List<FamilyMemberInfoDocument> familyList = new ArrayList<FamilyMemberInfoDocument>();
        FamilyMemberInfoDocument familyInfo = new FamilyMemberInfoDocument();
        familyInfo.setId("6eda5afd-6c5f-42c0-91de-653fb04f9901");
        familyInfo.setName("姓名");
        familyInfo.setMedicalNum("M000000000");
        familyInfo.setPhone("13600000000");
        familyInfo.setIdCard("210106000000000000");
        familyList.add(familyInfo);
        userInfoDocument.setFamilyMemberInfo(familyList);
        userInfoService.saveOrUpdateUserInfo(userInfoDocument);

        RegistrationDocument registrationDocument = new RegistrationDocument();
        registrationDocument.setNum("1");
        registrationDocument.setPatientId("58aaa76c5595753d58b78479");
        registrationDocument.setPatientPhone("13600000000");
        registrationService.saveRegistrationDocument(registrationDocument);
        registrationDocument = new RegistrationDocument();
        registrationDocument.setNum("2");
        registrationDocument.setPatientId("58aaa76c5595753d58b78111");
        registrationDocument.setPatientPhone("13600000002");
        registrationService.saveRegistrationDocument(registrationDocument);

        RecipeOrderDocument recipeOrderDocument = new RecipeOrderDocument();
        recipeOrderDocument.setPatientId("58aaa76c5595753d58b78479");
        recipeOrderDocument.setOperatorPhone("13600000000");
        recipeOrderRepository.save(recipeOrderDocument);
        recipeOrderDocument = new RecipeOrderDocument();
        recipeOrderDocument.setPatientId("58aaa76c5595753d58b78111");
        recipeOrderDocument.setOperatorPhone("13600000002");
        recipeOrderRepository.save(recipeOrderDocument);

        MessagesDocument messagesDocument = new MessagesDocument();
        messagesDocument.setUserId("6eda5afd-6c5f-42c0-91de-653fb04f990b");
        messagesDocument.setUserName("13600000000");
        messagesDocument.setDate("2016-12-22 10:38");
        messagesDocument.setContent("挂号成功");
        messagesService.saveMessage(messagesDocument);
        messagesDocument = new MessagesDocument();
        messagesDocument.setUserId("6eda5afd-6c5f-42c0-91de-1");
        messagesDocument.setUserName("13600000001");
        messagesDocument.setDate("2016-12-23 10:38");
        messagesDocument.setContent("挂号成功");
        messagesService.saveMessage(messagesDocument);
    }
}
