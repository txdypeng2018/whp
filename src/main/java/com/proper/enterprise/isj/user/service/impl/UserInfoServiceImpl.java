package com.proper.enterprise.isj.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;

/**
 * Created by think on 2016/8/15 0015.
 */
@Service
public class UserInfoServiceImpl {

	@Autowired
	UserInfoRepository userInfoRepository;

	@Autowired
	UserService userService;

	public UserInfoDocument saveUserAndUserInfo(UserEntity user, UserInfoDocument userInfo) throws Exception {
		User us = userService.save(user);
		userInfo.setUserId(us.getId());
		return userInfoRepository.save(userInfo);
	}

}
