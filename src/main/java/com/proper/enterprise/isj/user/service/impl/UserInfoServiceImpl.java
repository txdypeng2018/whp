package com.proper.enterprise.isj.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.authentication.SaveUserInfoAndUserFunction;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;

/**
 * 用户信息服务.
 * Created by think on 2016/8/15 0015.
 */
@Service
public class UserInfoServiceImpl extends AbstractService{

    @Autowired
    UserService userService;

    public UserInfoDocument saveUserAndUserInfo(UserEntity user, UserInfoDocument userInfo) throws Exception {
        return toolkit.executeFunction(SaveUserInfoAndUserFunction.class, user, userInfo);
    }

}
