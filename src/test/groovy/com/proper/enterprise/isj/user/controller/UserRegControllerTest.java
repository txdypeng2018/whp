package com.proper.enterprise.isj.user.controller;

import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.test.AbstractTest;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by think on 2016/8/25 0025.
 *
 */

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


}