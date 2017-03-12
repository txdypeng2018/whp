package com.proper.enterprise.isj.admin.controller;

import com.proper.enterprise.isj.log.document.WSLogDocument;
import com.proper.enterprise.isj.log.repository.WSLogRepository;
import com.proper.enterprise.platform.core.entity.DataTrunk;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.test.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

public class AdminWsLogControllerTest extends AbstractTest {

    @Autowired
    private WSLogRepository repository;

    @Test
    public void testWsLog() throws Exception {
        MvcResult result = get("/logview/wsLog?pageNo=1&pageSize=5&search=aaa&startDate=2017-01-01 10:00:00&endDate=2017-01-01 10:01:00", HttpStatus.OK);
        String resultContent = result.getResponse().getContentAsString();
        DataTrunk<Map<String, Object>> dataTrunk = JSONUtil.parse(resultContent, DataTrunk.class);
        Iterator<Map<String, Object>> iterator = dataTrunk.getData().iterator();
        assert dataTrunk.getCount() == 8;
        assert iterator.next().get("createTime") != null;
        result = get("/logview/wsLog?methodName=SMS&pageNo=1&pageSize=5", HttpStatus.OK);
        resultContent = result.getResponse().getContentAsString();
        dataTrunk = JSONUtil.parse(resultContent, DataTrunk.class);
        assert dataTrunk.getCount() == 1;
    }

    @After
    public void clearTestData() throws Exception {
        repository.deleteAll();
    }

    @Before
    public void saveTestData() throws Exception {
        repository.deleteAll();
        WSLogDocument wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 500);
        wSLogDocument.setId("586862a00000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 323);
        wSLogDocument.setId("586862dc0000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("netTest", null, "ccc", "bbb", 433);
        wSLogDocument.setId("586862a10000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("SMS", null, "aaa", "bbb", 4);
        wSLogDocument.setId("586862a10000000000000001");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 17);
        wSLogDocument.setId("586862e00000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 567);
        wSLogDocument.setId("586862a20000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 12);
        wSLogDocument.setId("586862a30000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 13);
        wSLogDocument.setId("586862a40000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 14);
        wSLogDocument.setId("586862a50000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 15);
        wSLogDocument.setId("586862a60000000000000000");
        repository.save(wSLogDocument);
        wSLogDocument = new WSLogDocument("getRegInfo", null, "aaa", "bbb", 16);
        wSLogDocument.setId("586862a70000000000000000");
        repository.save(wSLogDocument);
    }
}
