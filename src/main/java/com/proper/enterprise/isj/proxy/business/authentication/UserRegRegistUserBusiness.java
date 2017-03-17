package com.proper.enterprise.isj.proxy.business.authentication;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.HttpServletRequestContext;
import com.proper.enterprise.isj.context.HttpServletResponseContext;
import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.model.enums.SexTypeEnum;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.user.utils.MobileNoUtils;
import com.proper.enterprise.platform.api.auth.service.PasswordEncryptService;
import com.proper.enterprise.platform.auth.common.entity.UserEntity;
import com.proper.enterprise.platform.auth.jwt.service.JWTAuthcService;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class UserRegRegistUserBusiness<T, M extends MapParamsContext<Object> & HttpServletRequestContext<Object> & HttpServletResponseContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    PasswordEncryptService pwdService;

    @Autowired
    UserInfoService userInfoServiceImpl;

    @Autowired
    private JWTAuthcService jwtAuthcService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void process(M ctx) throws Exception {
        Map<String, String> userMsgMap = ctx.getParams();

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
                        debug("注册用户出现异常", e);
                        resultMsg = CenterFunctionUtils.USER_SAVE_ERROR;
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                }
            }
        }
        ctx.setResult(new ResponseEntity(resultMsg, httpStatus));
    }
}
