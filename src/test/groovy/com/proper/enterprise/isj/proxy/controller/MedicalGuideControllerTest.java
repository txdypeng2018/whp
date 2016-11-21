package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.platform.test.AbstractTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;

/**
 * Created by think on 2016/8/16 0016.
 */
public class MedicalGuideControllerTest extends AbstractTest {

    @Test
    @Sql
    public void testGetMedicalGuide() throws Exception {
        MvcResult result = get("/medicalGuide", HttpStatus.OK);
        assertEquals(result.getResponse().getContentAsString(), "medical guide message");
    }
}