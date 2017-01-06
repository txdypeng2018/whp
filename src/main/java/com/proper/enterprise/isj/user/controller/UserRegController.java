package com.proper.enterprise.isj.user.controller;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.isj.proxy.repository.BaseInfoRepository;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.model.enums.SexTypeEnum;
import com.proper.enterprise.isj.user.service.SMSService;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.user.utils.MobileNoUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.AuthcService;
import com.proper.enterprise.platform.api.auth.service.PasswordEncryptService;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.auth.jwt.service.JWTAuthcService;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by think on 2016/8/15 0015. 用户注册
 */
@RestController
@RequestMapping(path = "/permission")
public class UserRegController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserRegController.class);

    @Autowired
    UserInfoService userInfoServiceImpl;

    @Autowired
    UserService userService;

    @Autowired
    MessagesService messageService;

    @Autowired
    AuthcService authcService;

    @Autowired
    PasswordEncryptService pwdService;

    @Autowired
    SMSService smsService;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    BaseInfoRepository baseRepo;

    @Autowired
    private JWTAuthcService jwtAuthcService;

    /**
     * 发送验证码
     *
     * @param phone
     *            手机号
     * @param category
     *            category: 类别（1:注册;2:找回密码;3:重发）, phone:电话号码
     *
     * @return
     */
    @JWTIgnore
    @RequestMapping(path = "/verificationCode", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verificationCode(@RequestParam String phone, @RequestParam String category) {
        if (StringUtil.isNull(phone)) {
            throw new RuntimeException(CenterFunctionUtils.PHONE_NULL);
        }
        if (StringUtil.isNull(category)) {
            throw new RuntimeException(CenterFunctionUtils.PARAMETER_NULL);
        }
        if (!MobileNoUtils.isMobileNo(phone)) {
            throw new RuntimeException(CenterFunctionUtils.PHONE_ERROR);
        }

        User user = userService.getByUsername(phone);
        int cat = Integer.parseInt(category);
        if (cat == 1 && user != null) {
            throw new RuntimeException(CenterFunctionUtils.PHONE_EXIST);
        }
        if (cat == 2 && user == null) {
            throw new RuntimeException(CenterFunctionUtils.PHONE_NONEXIST);
        }

        String verificationCode = "";
        Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900);
        if(!category.equals(String.valueOf(3))){
            Cache.ValueWrapper valueWrapper =  tempCache.get("verificationcode_"+phone);
            if(valueWrapper!=null&&valueWrapper.get()!=null){
                verificationCode = (String)valueWrapper.get();
            }
        }
        if(StringUtil.isEmpty(verificationCode)){
            verificationCode = RandomStringUtils.randomNumeric(6);
            if(String.valueOf(Integer.parseInt(verificationCode)).length()<6){
                verificationCode = "1".concat(RandomStringUtils.randomNumeric(5));
            }
            String msgContent = String.format(CenterFunctionUtils.VERIFICATIONCODE_MSG, verificationCode);
            smsService.sendSMS(phone, msgContent);
            tempCache.put("verificationcode_" + phone, verificationCode);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * 注册
     */
    @JWTIgnore
    @RequestMapping(path = "/account", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> registUser(@RequestBody Map<String, String> userMsgMap, HttpServletRequest request,
            HttpServletResponse response) {
        String phone = userMsgMap.get("phone");
        String password = userMsgMap.get("password");
        String verificationCode = userMsgMap.get("verificationCode");
        String name = userMsgMap.get("name");
        String idCard = userMsgMap.get("idCard");
        String resultMsg = "";
        HttpStatus httpStatus = HttpStatus.CREATED;
        if (StringUtil.isEmpty(phone)) {
            resultMsg = CenterFunctionUtils.PHONE_NULL;
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (StringUtil.isEmpty(password)) {
            resultMsg = CenterFunctionUtils.PASSWORD_NULL;
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (StringUtil.isEmpty(verificationCode)) {
            resultMsg = CenterFunctionUtils.VERIFICATIONCODE_NULL;
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (StringUtil.isEmpty(name)) {
            resultMsg = CenterFunctionUtils.NAME_NULL;
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (StringUtil.isEmpty(idCard)) {
            resultMsg = CenterFunctionUtils.IDCARD_NULL;
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            boolean checkVer = false;
            String verKey = "";
            Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900);
            Cache.ValueWrapper valueWrapper =  tempCache.get("verificationcode_"+phone);
            if(valueWrapper!=null&&valueWrapper.get()!=null){
                verKey = (String)valueWrapper.get();
                if(verKey.equals(verificationCode)){
                    checkVer = true;
                }
            }
            if (!checkVer) {
                resultMsg = CenterFunctionUtils.VERIFICATIONCODE_ERROR;
                httpStatus = HttpStatus.BAD_REQUEST;
            } else if (!MobileNoUtils.isMobileNo(phone)) {
                resultMsg = CenterFunctionUtils.PHONE_VOID;
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                idCard = idCard.toUpperCase();
                if (!IdcardUtils.validateCard(idCard)) {
                    resultMsg = CenterFunctionUtils.IDCARD_ERROR;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    UserEntity user = new UserEntity();
                    user.setUsername(phone);
                    user.setPassword(pwdService.encrypt(password));
                    UserInfoDocument userInfo = new UserInfoDocument();
                    userInfo.setIdCard(idCard);
                    userInfo.setName(name);
                    userInfo.setPhone(phone);
                    userInfo.setPatientVisits(String.valueOf(1));
                    String sexType = IdcardUtils.getGenderByIdCard(idCard);
                    if (sexType.equals("M")) {
                        userInfo.setSexCode(String.valueOf(SexTypeEnum.MALE.getCode()));
                    } else if (sexType.equals("F")) {
                        userInfo.setSexCode(String.valueOf(SexTypeEnum.FEMALE.getCode()));
                    } else {
                        userInfo.setSexCode(String.valueOf(SexTypeEnum.SECRET.getCode()));
                    }
                    userInfo.setSex(CenterFunctionUtils.getSexMap().get(userInfo.getSexCode()));
                    try {
                        userInfoServiceImpl.saveUserAndUserInfo(user, userInfo);
                        resultMsg = jwtAuthcService.getUserToken(phone);
                        tempCache.evict("verificationcode_" + phone);
                    } catch (Exception e) {
                        LOGGER.debug("注册用户出现异常", e);
                        resultMsg = CenterFunctionUtils.USER_SAVE_ERROR;
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                }
            }
        }
        return new ResponseEntity(resultMsg, httpStatus);
    }

    /**
     * 找回/修改密码
     *
     * @param userMsgMap
     *            category "1:修改密码;2:找回密码"
     */
    @JWTIgnore
    @RequestMapping(path = "/account", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> modifyPassword(@RequestBody Map<String, String> userMsgMap) {
        String phone = userMsgMap.get("phone");
        String nowPassword = userMsgMap.get("nowPassword");
        String newPassword = userMsgMap.get("newPassword");
        String confirmPassword = userMsgMap.get("confirmPassword");
        String verificationCode = userMsgMap.get("verificationCode");
        String category = userMsgMap.get("category");

        String resultMsg = "";
        HttpStatus httpStatus = HttpStatus.OK;
        if (StringUtil.isEmpty(category)) {
            resultMsg = CenterFunctionUtils.PARAMETER_NULL;
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        User user = null;
        switch (Integer.parseInt(category)) {
        case 1:
            try {
                user = userService.getCurrentUser();
            } catch (Exception e) {
                LOGGER.debug("获得当前人失败", e);
                resultMsg = e.getMessage();
                httpStatus = HttpStatus.UNAUTHORIZED;
            }
            if (user != null) {
                if (StringUtil.isEmpty(nowPassword)) {
                    resultMsg = CenterFunctionUtils.NOWPASSWORD_NULL;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (StringUtil.isEmpty(newPassword)) {
                    resultMsg = CenterFunctionUtils.NEWPASSWORD_NULL;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (StringUtil.isEmpty(confirmPassword)) {
                    resultMsg = CenterFunctionUtils.CONFIRMPASSWORD_NULL;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!newPassword.equals(confirmPassword)) {
                    resultMsg = CenterFunctionUtils.NEWPASSWORD_DIFFERENT;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else if (!user.getPassword().equals(pwdService.encrypt(nowPassword))) {
                    resultMsg = CenterFunctionUtils.NOWPASSWORD_ERROR;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    try {
                        user.setPassword(pwdService.encrypt(newPassword));
                        userService.save(user);
                    } catch (Exception e) {
                        LOGGER.debug("修改密码出现异常", e);
                        resultMsg = CenterFunctionUtils.PASSWORD_MODIFY_ERROR;
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                }
            }

            break;
        case 2:
            if (StringUtil.isEmpty(phone)) {
                resultMsg = CenterFunctionUtils.PHONE_NULL;
                httpStatus = HttpStatus.BAD_REQUEST;
            } else if (StringUtil.isEmpty(verificationCode)) {
                resultMsg = CenterFunctionUtils.VERIFICATIONCODE_NULL;
                httpStatus = HttpStatus.BAD_REQUEST;
            } else if (StringUtil.isEmpty(newPassword)) {
                resultMsg = CenterFunctionUtils.NEWPASSWORD_NULL;
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                boolean checkVer = false;
                String verKey = "";
                Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900);
                Cache.ValueWrapper valueWrapper =  tempCache.get("verificationcode_"+phone);
                if(valueWrapper!=null&&valueWrapper.get()!=null){
                    verKey = (String)valueWrapper.get();
                    if(verKey.equals(verificationCode)){
                        checkVer = true;
                    }
                }
                if (!checkVer) {
                    resultMsg = CenterFunctionUtils.VERIFICATIONCODE_ERROR;
                    httpStatus = HttpStatus.BAD_REQUEST;
                } else {
                    user = userService.getByUsername(phone);
                    try {
                        user.setPassword(pwdService.encrypt(newPassword));
                        userService.save(user);
                        tempCache.evict("verificationcode_" + phone);
                    } catch (Exception e) {
                        LOGGER.debug("找回密码出现异常", e);
                        resultMsg = CenterFunctionUtils.PASSWORD_MODIFY_ERROR;
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                }
            }
            break;
        default:
            resultMsg = CenterFunctionUtils.PARAMETER_NULL;
            httpStatus = HttpStatus.BAD_REQUEST;
            break;
        }
        return new ResponseEntity(resultMsg, httpStatus);
    }

    /**
     * 用户登录
     *
     * @param phoneMap
     *            包含手机号和密码
     * @return 登录用户
     */
    @JWTIgnore
    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> login(@RequestBody Map<String, String> phoneMap) {
        String phone = phoneMap.get("phone");
        String password = phoneMap.get("password");
        if (!authcService.authenticate(phone, password)) {
            throw new RuntimeException(CenterFunctionUtils.LOGIN_ERROR);
        }

        try {
            return responseOfPost(jwtAuthcService.getUserToken(phone));
        } catch (Exception e) {
            LOGGER.error("Login ERROR!", e);
            throw new RuntimeException(CenterFunctionUtils.LOGIN_ERROR);
        }
    }

    /**
     * 取得注册提示信息
     *
     * @return 注册提示信息
     */
    @JWTIgnore
    @RequestMapping(path = "/loginPrompt", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLoginPrompt() {
        List<BaseInfoEntity> infoList = baseRepo.findByInfoType(ConfCenter.get("isj.info.loginPrompt"));
        String guideMsg = "";
        guideMsg = infoList.get(0).getInfo();
        return responseOfGet(guideMsg);
    }
}
