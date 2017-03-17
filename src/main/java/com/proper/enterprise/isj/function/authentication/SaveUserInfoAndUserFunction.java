package com.proper.enterprise.isj.function.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class SaveUserInfoAndUserFunction implements IFunction<UserInfoDocument> {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    UserService userService;

    @Override
    public UserInfoDocument execute(Object... params) throws Exception {
        return saveUserAndUserInfo((UserEntity) params[0], (UserInfoDocument) params[1]);
    }

    public UserInfoDocument saveUserAndUserInfo(UserEntity user, UserInfoDocument userInfo) throws Exception {
        User us = userService.save(user);
        userInfo.setUserId(us.getId());
        return userInfoRepository.save(userInfo);
    }

}
