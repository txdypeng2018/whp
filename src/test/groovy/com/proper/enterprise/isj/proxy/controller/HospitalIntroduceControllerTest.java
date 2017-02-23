package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.platform.test.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Created by think on 2016/8/16 0016.
 */
public class HospitalIntroduceControllerTest extends AbstractTest {

    @Autowired
    HospitalIntroduceService hospitalIntroduceService;

    @Test
    public void testGetHospitalIntroduce() throws Exception {
        get("/hospitalIntroduce", HttpStatus.OK);
    }
}