package com.proper.enterprise.isj.proxy.controller

import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

/**
 * 温馨提示测试类
 */
@Sql
class PromptTipsControllerTest extends AbstractTest {

    @Test
    public void testGetPromptTips() {
        mockUser('id', 'name', 'pwd', true);
        String obj = (String) getAndReturn("/prompt/tips?pageNo=1&pageSize=5&infoType=A", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(obj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        String value = retList.get(0).get("info");
        assert count == 6
        assert retList.size() == 5;
        assert value == "this is value 1"
    }

    @Test
    public void testGetDetailPromptTip() {
        mockUser('id', 'name', 'pwd', true);
        String obj = (String) getAndReturn("/prompt/tips/90415726-95d6-11e6-83da-fbb35e195804", "", HttpStatus.OK);
        Map<String, String> doc = JSONUtil.parse(obj, Object.class);
        String value = String.valueOf(doc.get("info"));
        assert value == "this is value 1"
    }

    @Test
    public void testDelPromptTips() {
        mockUser('id', 'name', 'pwd', true);
        delete("/prompt/tips?ids=90415726-95d6-11e6-83da-fbb35e195804,90417e37-95d6-11e6-83da-fbb35e195804", HttpStatus.NO_CONTENT);
        String retObj = (String) getAndReturn("/prompt/tips?pageNo=1&pageSize=20", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        assert count == 9
    }

    @Test
    public void testAddPromptTips() {
        mockUser('id', 'name', 'pwd', true);
        post("/prompt/tips", '{"infoType": "test", "typeName": "测试", "info": "this is test value!", "createUserId": "testUserId"}', HttpStatus.CREATED);
        String retObj = (String) getAndReturn("/prompt/tips?pageNo=1&pageSize=20", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        assert count == 12
        String retObj2 = (String) getAndReturn("/prompt/tips?pageNo=1&pageSize=20&infoType=test", "", HttpStatus.OK);
        Map<String, Object> doc2 = JSONUtil.parse(retObj2, Object.class);
        int count2 = Integer.parseInt(String.valueOf(doc2.get("count")));
        assert count2 == 1
        List<Map<String, String>> retList2 = (List<Map<String, Object>>) doc2.get("data");
        String value3 = retList2.get(0).get("info");
        assert value3 == "this is test value!"
    }

    @Test
    public void testUpdatePromptTips() {
        mockUser('id', 'name', 'pwd', true);
        put("/prompt/tips", '{"id":"90415726-95d6-11e6-83da-fbb35e195804", "infoType": "MEDICALGUIDE", "typeName": "就医指南", "info": "this is update test value!"}', HttpStatus.OK);
        String retObj = (String) getAndReturn("/prompt/tips?pageNo=1&pageSize=20&infoType=MEDICALGUIDE", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        String value = retList.get(0).get("info");
        assert count == 1
        assert value == "this is update test value!"

    }

}
