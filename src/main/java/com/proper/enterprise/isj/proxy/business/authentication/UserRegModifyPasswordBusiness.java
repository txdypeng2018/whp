package com.proper.enterprise.isj.proxy.business.authentication;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.PasswordEncryptService;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class UserRegModifyPasswordBusiness<M extends MapParamsContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncryptService pwdService;

    @Autowired
    CacheManager cacheManager;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void process(M ctx) throws Exception {
        Map<String, String> userMsgMap = ctx.getParams();

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
                debug("获得当前人失败", e);
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
                        debug("修改密码出现异常", e);
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
                Cache.ValueWrapper valueWrapper = tempCache.get("verificationcode_" + phone);
                if (valueWrapper != null && valueWrapper.get() != null) {
                    verKey = (String) valueWrapper.get();
                    if (verKey.equals(verificationCode)) {
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
                        debug("找回密码出现异常", e);
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
        ctx.setResult(new ResponseEntity(resultMsg, httpStatus));
    }
}
