package com.proper.enterprise.isj.proxy.business.authentication;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.CategoryContext;
import com.proper.enterprise.isj.context.PhoneContext;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.service.SMSService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.MobileNoUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class UserRegVerificationCodeBusiness<T, M extends CategoryContext<Object> & PhoneContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M> {
    @Autowired
    UserService userService;

    @Autowired
    SMSService smsService;

    @Autowired
    CacheManager cacheManager;

    @Override
    public void process(M ctx) throws Exception {
        String phone = ctx.getPhone();
        String category = ctx.getCategory();

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
        if (!category.equals(String.valueOf(3))) {
            Cache.ValueWrapper valueWrapper = tempCache.get("verificationcode_" + phone);
            if (valueWrapper != null && valueWrapper.get() != null) {
                verificationCode = (String) valueWrapper.get();
            }
        }
        if (StringUtil.isEmpty(verificationCode)) {
            verificationCode = RandomStringUtils.randomNumeric(6);
            if (String.valueOf(Integer.parseInt(verificationCode)).length() < 6) {
                verificationCode = "1".concat(RandomStringUtils.randomNumeric(5));
            }
            String msgContent = String.format(CenterFunctionUtils.VERIFICATIONCODE_MSG, verificationCode);
            smsService.sendSMS(phone, msgContent);
            tempCache.put("verificationcode_" + phone, verificationCode);
        }
        ctx.setResult(HttpStatus.OK);
    }
}
