package com.proper.enterprise.isj.user.controller;

import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户信息基础控制器.
 * Created by think on 2016/9/2 0002.
 */

@RestController
@RequestMapping(path = "/dataBase")
public class UserDataBaseController extends BaseController {

    @Autowired
    UserService userService;
    @Autowired
    UserInfoService userInfoService;

    @RequestMapping(path = "/familyMenberTypes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Map<String, String>>> getFamilyMenberTypes() throws Exception {
        User user = userService.getCurrentUser();
        String sexCode = "";
        if (user != null) {
            UserInfoDocument userInfo = userInfoService.getUserInfoByUserId(user.getId());
            if (userInfo != null) {
                sexCode = userInfo.getSexCode();
            }
        }
        return responseOfGet(CenterFunctionUtils.getFamilyMenberTypeMap(sexCode));
    }

    @AuthcIgnore
    @RequestMapping(path = "/sexTypes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Map<String, String>>> getSexTypes() {
        return responseOfGet(CenterFunctionUtils.getSexCodeMap());
    }

    @RequestMapping(path = "/familyMenberTypes/{typeCode}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> getFamilyMenberTypes(@PathVariable String typeCode) throws Exception {
        User user = userService.getCurrentUser();
        String sexCode = "";
        if (user != null) {
            UserInfoDocument userInfo = userInfoService.getUserInfoByUserId(user.getId());
            if (userInfo != null) {
                sexCode = userInfo.getSexCode();
            }
        }
        return responseOfGet(CenterFunctionUtils.getFamilyMenberTypeMap(sexCode).get(typeCode));
    }

    @AuthcIgnore
    @RequestMapping(path = "/feedbackTypes", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, String>>> getFeedbackTypes() {
        return responseOfGet(CenterFunctionUtils.getFeedbackListMap());
    }
}
