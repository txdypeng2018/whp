package com.proper.enterprise.isj.proxy.controller

import com.proper.enterprise.isj.proxy.document.MessagesDocument
import com.proper.enterprise.isj.proxy.service.MessagesService
import com.proper.enterprise.isj.user.service.UserInfoPublicServiceTest
import com.proper.enterprise.isj.user.service.UserInfoService
import com.proper.enterprise.platform.api.auth.model.User
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MvcResult

class MessagesControllerTest  extends AbstractTest {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserInfoPublicServiceTest userInfoPublicServiceTest;

    @Autowired
    MessagesService messagesService;

    @Test
    public void messagesTest() {
        println 1
//        User user = userInfoPublicServiceTest.saveUser();
//        String token = userInfoService.userLogin(user);
//        mockRequest.addHeader("Authorization", token);
//
//        MessagesDocument testDocument = new MessagesDocument();
//        testDocument.setContent("测试单个推送!");
//        testDocument.setUserId(user.getId());
//        testDocument.setUserName(user.getUsername());
//        messagesService.saveMessage(testDocument);
//        System.out.println("成功测试单个推送!");
//
//        List<MessagesDocument> testDocumentList = new ArrayList<MessagesDocument>();
//        MessagesDocument testDocument1 = new MessagesDocument();
//        testDocument1.setContent("测试多个推送!");
//        testDocument1.setUserId(user.getId());
//        testDocument1.setUserName(user.getUsername());
//        testDocumentList.add(testDocument1);
//        messagesService.saveMessages(testDocumentList);
//        System.out.println("成功测试多个推送!");
//
//        MvcResult result = get("/messages", HttpStatus.OK);
//        System.out.println("success!");
//        System.out.println(result.getResponse().getContentAsString());
    }
}
