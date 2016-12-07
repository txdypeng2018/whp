package com.proper.enterprise.isj.user.service;

import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.PasswordEncryptService;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by think on 2016/8/29 0029.
 *
 */
@Service
public class UserInfoPublicServiceTest {

    @Autowired
    UserService userServiceTestImpl;

    @Autowired
    UserInfoService userInfoServiceImpl;

    @Autowired
    PasswordEncryptService pwdService;

    public User saveUser() {
        // mockUser();
        for (int i = 0; i < 1; i++) {
            UserEntity user = new UserEntity();
            user.setUsername("13800000000");
            user.setPassword(pwdService.encrypt("123456"));
            user.setEmail(user.getUsername() + "@qq.com");
            user.setSuperuser(false);

            User us = userServiceTestImpl.save(user);
            UserInfoDocument info = new UserInfoDocument();
            info.setUserId(us.getId());
            info.setName("测试" + i);
            info.setPhone("13800000000");
            info.setIdCard("210");
            userInfoServiceImpl.saveOrUpdateUserInfo(info);
        }
        User user = userServiceTestImpl.getByUsername("13800000000");
        return user;
    }
}
