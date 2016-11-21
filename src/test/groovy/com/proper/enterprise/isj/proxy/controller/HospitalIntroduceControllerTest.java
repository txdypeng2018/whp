package com.proper.enterprise.isj.proxy.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import com.proper.enterprise.isj.proxy.service.HospitalIntroduceService;
import com.proper.enterprise.platform.test.AbstractTest;

/**
 * Created by think on 2016/8/16 0016.
 */
public class HospitalIntroduceControllerTest extends AbstractTest {

	@Autowired
	HospitalIntroduceService hospitalIntroduceService;

	@Test
	public void testGetHospitalIntroduce() throws Exception {
		MvcResult result = get("/hospitalIntroduce", HttpStatus.OK);
		System.out.println(result.getResponse().getContentAsString());
	}
}