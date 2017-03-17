package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.test.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

/**
 * Created by think on 2016/8/16 0016.
 */
public class RegisterControllerTest extends AbstractTest {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Test
    public void testGetRegistration() throws Exception {
        // doc.setAmount("123");
        // RegistrationDocument saved = registrationRepository.save(doc);
        get("/register/registration?id=123123", HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPutRegistration() throws Exception {
        registrationRepository.deleteAll();
        userInfoRepository.deleteAll();

        String idCard = "510115196806189103";

        String userId = "012d29db-aef9-4969-9c69-07ecb28902ab";
        mockUser(userId);
        UserInfoDocument user = new UserInfoDocument();
        user.setPatientVisits("1");
        user.setUserId(userId);
        user.setIdCard(idCard);
        user.setName(idCard);
        user.setPhone("1234567");
        user.setSex("0");
        List<FamilyMemberInfoDocument> familyList = new ArrayList<>();
        FamilyMemberInfoDocument family = new FamilyMemberInfoDocument();
        family.setId("57c9467130cca21f4caccfc2");
        family.setSexCode("0");
        family.setName(idCard);
        family.setPhone("1234567");
        family.setIdCard(idCard);
        familyList.add(family);
        user.setFamilyMemberInfo(familyList);
        userInfoService.saveOrUpdateUserInfo(user);

        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("amount", "");
        jsonMap.put("clinicCategoryCode", "");
        jsonMap.put("deptId", "");
        jsonMap.put("doctorId", "011193");
        jsonMap.put("isAppointment", "1");
        jsonMap.put("patientId", "57c9467130cca21f4caccfc2");
        jsonMap.put("payStatus", "0");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH,  +2);
        jsonMap.put("registerDate", DateUtil.toString(cal.getTime(), "yyyy-MM-dd").concat(" 08:00"));
        MvcResult result = post("/register/registrations/registration", MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON, JSONUtil.toJSON(jsonMap), HttpStatus.CREATED);

        String obj = result.getResponse().getContentAsString();
        Map<String, Object> doc = (Map<String, Object>)JSONUtil.parse(obj, Object.class);

        assert doc.get("registrationId") != null;
        assert doc.get("orderNum") != null;
    }
}