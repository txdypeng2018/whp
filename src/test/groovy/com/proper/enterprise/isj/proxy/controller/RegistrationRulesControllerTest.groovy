package com.proper.enterprise.isj.proxy.controller

import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

/**
 * Web 挂号规则测试类
 */
@Sql
class RegistrationRulesControllerTest  extends AbstractTest {

    @Test
    public void testGetRegistrationRules() {
        mockUser('id', 'name', 'pwd', true);
        String obj = (String) getAndReturn("/registration/rules?pageNo=1&pageSize=5&catalogue=REG_RES&rule=isChild", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(obj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        String value = retList.get(0).get("name");
        assert count == 1
        assert value == "儿童挂号约束"
    }

    @Test
    public void testGetDetailRegistrationRule() {
        mockUser('id', 'name', 'pwd', true);
        String obj = (String) getAndReturn("/registration/rules/tsf1", "", HttpStatus.OK);
        Map<String, String> doc = JSONUtil.parse(obj, Object.class);
        String value = String.valueOf(doc.get("name"));
        assert value == "学科列表过滤规则"
    }

    @Test
    public void testDelRegistrationRules() {
        mockUser('id', 'name', 'pwd', true);
        delete("/registration/rules?ids=tr1,tr3", HttpStatus.NO_CONTENT);
        String retObj = (String) getAndReturn("/registration/rules?pageNo=1&pageSize=20", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        assert count == 4
    }

    @Test
    public void testAddRegistrationRules() {
        mockUser('id', 'name', 'pwd', true);
        post("/registration/rules", '{"catalogue": "test", "name": "测试", "rule": "this is test value!", "createUserId": "testUserId"}', HttpStatus.CREATED);
        String retObj = (String) getAndReturn("/registration/rules?pageNo=1&pageSize=20", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        assert count == 7
        String retObj2 = (String) getAndReturn("/registration/rules?pageNo=1&pageSize=20&catalogue=test", "", HttpStatus.OK);
        Map<String, Object> doc2 = JSONUtil.parse(retObj2, Object.class);
        int count2 = Integer.parseInt(String.valueOf(doc2.get("count")));
        assert count2 == 1
        List<Map<String, String>> retList2 = (List<Map<String, Object>>) doc2.get("data");
        String value3 = retList2.get(0).get("rule");
        assert value3 == "this is test value!"
    }

    @Test
    public void testUpdateRegistrationRules() {
        mockUser('id', 'name', 'pwd', true);
        put("/registration/rules", '{"id":"tsf1", "catalogue": "test_catalogue", "name": "测试名称", "rule": "this is update test value!"}', HttpStatus.OK);
        String retObj = (String) getAndReturn("/registration/rules?pageNo=1&pageSize=20&catalogue=test_catalogue", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        String value = retList.get(0).get("rule");
        assert count == 1
        assert value == "this is update test value!"

    }

}
