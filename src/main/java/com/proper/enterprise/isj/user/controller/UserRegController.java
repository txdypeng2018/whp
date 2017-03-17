package com.proper.enterprise.isj.user.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.CategoryContext;
import com.proper.enterprise.isj.context.HttpServletRequestContext;
import com.proper.enterprise.isj.context.HttpServletResponseContext;
import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.context.PhoneContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.authentication.UserRegGetLoginPromptBusiness;
import com.proper.enterprise.isj.proxy.business.authentication.UserRegLoginBusiness;
import com.proper.enterprise.isj.proxy.business.authentication.UserRegModifyPasswordBusiness;
import com.proper.enterprise.isj.proxy.business.authentication.UserRegRegistUserBusiness;
import com.proper.enterprise.isj.proxy.business.authentication.UserRegVerificationCodeBusiness;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * Created by think on 2016/8/15 0015. 用户注册
 */
@RestController
@RequestMapping(path = "/permission")
public class UserRegController extends IHosBaseController {

    /**
     * 发送验证码
     *
     * @param phone
     *            手机号
     * @param category
     *            category: 类别（1:注册;2:找回密码;3:重发）, phone:电话号码
     * @return
     */
    @AuthcIgnore
    @RequestMapping(path = "/verificationCode", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verificationCode(@RequestParam String phone, @RequestParam String category) {
        return new ResponseEntity<>(toolkit.execute(UserRegVerificationCodeBusiness.class, (c) -> {
            ((PhoneContext<?>) c).setPhone(phone);
            ((CategoryContext<?>) c).setCategory(category);
        }));
    }

    /**
     * 注册
     */
    @AuthcIgnore
    @RequestMapping(path = "/account", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> registUser(@RequestBody Map<String, String> userMsgMap, HttpServletRequest request,
            HttpServletResponse response) {
        return toolkit.execute(UserRegRegistUserBusiness.class, c -> {
            ((MapParamsContext<?>) c).setParams(userMsgMap);
            ((HttpServletRequestContext<?>) c).setRequest(request);
            ((HttpServletResponseContext<?>) c).setResponse(response);
        });

    }

    /**
     * 找回/修改密码
     *
     * @param userMsgMap
     *            category "1:修改密码;2:找回密码"
     */
    @AuthcIgnore
    @RequestMapping(path = "/account", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> modifyPassword(@RequestBody Map<String, String> userMsgMap) {
        return toolkit.execute(UserRegModifyPasswordBusiness.class, c -> {
            ((MapParamsContext<?>) c).setParams(userMsgMap);
        });

    }

    /**
     * 用户登录
     *
     * @param phoneMap
     *            包含手机号和密码
     * @return 登录用户
     */
    @AuthcIgnore
    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> login(@RequestBody Map<String, String> phoneMap) {
        return responseOfPost(toolkit.execute(UserRegLoginBusiness.class, (c) -> {
            ((MapParamsContext<?>) c).setParams(phoneMap);
        }));

    }

    /**
     * 取得注册提示信息
     *
     * @return 注册提示信息
     */
    @AuthcIgnore
    @RequestMapping(path = "/loginPrompt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLoginPrompt() {
        return responseOfGet(toolkit.execute(UserRegGetLoginPromptBusiness.class, c -> {
        }));
    }
}
