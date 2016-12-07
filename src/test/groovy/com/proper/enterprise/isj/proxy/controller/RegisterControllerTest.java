package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.test.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by think on 2016/8/16 0016.
 */
public class RegisterControllerTest extends AbstractTest {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    RegistrationRepository registrationRepository;

    @Test
    public void testGetRegistration() throws Exception {
        // doc.setAmount("123");
        // RegistrationDocument saved = registrationRepository.save(doc);
        get("/register/registration?id=123123", HttpStatus.NOT_FOUND);
    }

    // @Test
    public void testPutRegistration() throws Exception {
        mockUser("012d29db-aef9-4969-9c69-07ecb28902ab", "13800000000");
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("amount", "");
        jsonMap.put("clinicCategoryCode", "");
        jsonMap.put("deptId", "");
        jsonMap.put("doctorId", "011193");
        jsonMap.put("isAppointment", "1");
        jsonMap.put("patientId", "57c9467130cca21f4caccfc2");
        jsonMap.put("payStatus", "0");
        jsonMap.put("registerDate", "2016-09-05 09:00");
        post("/register/registrations/registration", MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON, JSONUtil.toJSON(jsonMap), HttpStatus.OK);
    }
}