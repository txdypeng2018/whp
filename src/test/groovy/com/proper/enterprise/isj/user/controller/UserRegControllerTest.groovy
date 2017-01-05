package com.proper.enterprise.isj.user.controller
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql

public class UserRegControllerTest extends AbstractTest {

    @Test
    public void testCheckRegistTelephone() throws Exception {
        get("/permission/verificationCode?phone=13800000001&category=1", HttpStatus.OK);
    }

    @Test
    public void testRegistUser() throws Exception {
        testCheckRegistTelephone();
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("phone", "13800000001");
        jsonMap.put("password", "13800000001");
        jsonMap.put("verificationCode", "111111");
        jsonMap.put("name", "13800000001");
        jsonMap.put("idCard", "21010519870512535X");
        post("/permission/account", MediaType.TEXT_PLAIN,
                JSONUtil.toJSON(jsonMap), HttpStatus.BAD_REQUEST);
    }

    @Test
    @Sql
    public void login() {
        def t1 = post('/permission/login', '{"phone":"abc", "password":"123456"}', HttpStatus.CREATED).response.contentAsString
        def t2 = post('/permission/login', '{"phone":"abc", "password":"123456"}', HttpStatus.CREATED).response.contentAsString
        assert t1 != t2

        t1 = post('/permission/login', '{"phone":"hasrole", "password":"123456"}', HttpStatus.CREATED).response.contentAsString
        t2 = post('/permission/login', '{"phone":"hasrole", "password":"123456"}', HttpStatus.CREATED).response.contentAsString
        assert t1 == t2
    }

}