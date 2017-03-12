package com.proper.enterprise.isj.proxy.controller

import static com.proper.enterprise.platform.core.PEPConstants.DEFAULT_TIMESTAMP_FORMAT

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

import com.mongodb.client.MongoDatabase
import com.proper.enterprise.isj.user.repository.UserInfoRepository
import com.proper.enterprise.platform.core.utils.DateUtil
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.test.AbstractTest

/**
 * Web 挂号规则测试类
 */
@Sql
class RegistrationRulesControllerTest extends AbstractTest {


    @Autowired
    UserInfoRepository userInfoRepository

    @Autowired
    MongoDatabase mongoDatabase


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
        assert doc2["data"][0]["lastModifyUserName"] == "name"
        assert doc2["data"][0]["rule"] == "this is test value!"
    }

    static String ruleDescription = "This is a description."
    static String howToUse = "This is a HowToUse message."

    @Test
    public void testAddRegistrationRulesWithDesc() {
        def startTm = new Date() //记录修改之前的时间
        mockUser('id', 'name', 'pwd', true)
        post("/registration/rules", '{"catalogue": "test", "name": "测试", "rule": "this is test value!", "createUserId": "testUserId", "description":"' + ruleDescription + '", "howToUse":"'+ howToUse+ '"}', HttpStatus.CREATED);
        String retObj = (String) getAndReturn("/registration/rules?pageNo=1&pageSize=20", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        assert count == 7
        String retObj2 = (String) getAndReturn("/registration/rules?pageNo=1&pageSize=20&catalogue=test", "", HttpStatus.OK);
        Map<String, Object> doc2 = JSONUtil.parse(retObj2, Object.class);
        int count2 = Integer.parseInt(String.valueOf(doc2.get("count")));
        assert count2 == 1
        List<Map<String, String>> retList2 = (List<Map<String, Object>>) doc2.get("data");
        def endTm = new Date() //记录修改之后的时间
        def rule = retList2.get(0)
        String value3 = rule.get("rule");
        assert value3 == "this is test value!"
        def desc = rule.get("description")
        def modifier = rule.get("lastModifyUserId")
        def modifyTime = rule.get("lastModifyTime")
        def how2use = rule.get("howToUse")
        assert ruleDescription == desc //确认描述与输入信息一致
        assert rule.lastModifyUserName == "name"
        def modifyTm = DateUtil.toDate(modifyTime, DEFAULT_TIMESTAMP_FORMAT) //获得记录的修改时间
        assert endTm.minus(modifyTm) >= 0 && modifyTm.minus(startTm) >= 0
        //确认记录修改时间在结束时间之前，开始时间之后
        assert "id" == modifier //确认最后修改人与操作人一致
        assert how2use == howToUse
    }

    @Test
    public void testUpdateRegistrationRules() {
        mockUser('id', 'name', 'pwd', true);
        def startTm = new Date() //记录修改之前的时间
        put("/registration/rules", '{"id":"tsf1", "catalogue": "test_catalogue", "name": "测试名称", "rule": "this is update test value!" , "description":"' + ruleDescription + '", "howToUse":"'+ howToUse+ '"}', HttpStatus.OK);
        def endTm = new Date() //记录修改之后的时间
        String retObj = (String) getAndReturn("/registration/rules?pageNo=1&pageSize=20&catalogue=test_catalogue", "", HttpStatus.OK);
        Map<String, Object> doc = JSONUtil.parse(retObj, Object.class);
        int count = Integer.parseInt(String.valueOf(doc.get("count")));
        List<Map<String, String>> retList = (List<Map<String, Object>>) doc.get("data");
        def rule = retList.get(0)
        String value = rule.get("rule")
        assert count == 1
        assert value == "this is update test value!"
        def desc = rule.get("description")
        def modifier = rule.get("lastModifyUserId")
        def modifyTime = rule.get("lastModifyTime")
        def how2use = rule.get("howToUse")
        assert ruleDescription == desc //确认描述与输入信息一致
        def modifyTm = DateUtil.toDate(modifyTime, DEFAULT_TIMESTAMP_FORMAT) //获得记录的修改时间
        assert rule.lastModifyUserName == "name"
        assert endTm.minus(modifyTm) >= 0 && modifyTm.minus(startTm) >= 0
        //确认记录修改时间在结束时间之前，开始时间之后
        assert "id" == modifier //确认最后修改人与操作人一致
        assert how2use == howToUse
    }

}
