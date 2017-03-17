package com.proper.enterprise.isj.function.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.repository.UserInfoRepository;
import com.proper.enterprise.isj.user.service.impl.UserInfoServiceImpl;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SaveUserAndUserInfoFunction implements IFunction<UserInfoDocument>, ILoggable {

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    UserInfoServiceImpl userInfoServiceImpl;

    @Autowired
    UserService userService;

    @Override
    public UserInfoDocument execute(Object... params) throws Exception {
        return saveUserAndUserInfo((UserEntity) params[0], (UserInfoDocument) params[1]);
    }

    public UserInfoDocument saveUserAndUserInfo(UserEntity user, UserInfoDocument userInfo) throws Exception {
        try {
            userInfo = toolkitx.executeRepositoryFunction(UserInfoRepository.class, "save", userInfo);
            userInfo = userInfoServiceImpl.saveUserAndUserInfo(user, userInfo);
        } catch (Exception e) {
            debug("保存用户信息出现异常", e);
            if (StringUtil.isNotEmpty(userInfo.getUserId())) {
                User us = userService.get(userInfo.getUserId());
                if (us == null) {
                    toolkitx.executeRepositoryFunction(UserInfoRepository.class, "delete", userInfo);
                }
            }
            throw e;
        }

        return userInfo;
    }

}
