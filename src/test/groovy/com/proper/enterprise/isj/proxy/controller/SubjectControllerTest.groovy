package com.proper.enterprise.isj.proxy.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.proper.enterprise.isj.proxy.document.SubjectDocument
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MvcResult

public class SubjectControllerTest extends AbstractTest {

    @Test
    @Sql
    public void testGetSubject() throws Exception {
        assert getSubList("/subjects?districtId=1207&type=2").size() == 3
        assert getSubList("/subjects?districtId=1207&type=1").size() == 2
        assert getSubList("/subjects?districtId=1207&type=1")[1].subjects.size() == 2
    }

    private List<SubjectDocument> getSubList(String url) throws Exception {
        MvcResult result = get(url, HttpStatus.OK);
        String resStr = result.getResponse().getContentAsString();
        return JSONUtil.getMapper().readValue(resStr, new TypeReference<List<SubjectDocument>>() { });
    }

}