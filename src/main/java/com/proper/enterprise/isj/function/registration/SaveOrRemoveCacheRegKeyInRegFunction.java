package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class SaveOrRemoveCacheRegKeyInRegFunction implements IFunction<Object> {

    @Autowired
    CacheManager cacheManager;

    @Override
    public Object execute(Object... params) throws Exception {
        saveOrRemoveCacheRegKey((RegistrationDocument) params[0], (String) params[1]);
        return null;
    }

    public void saveOrRemoveCacheRegKey(RegistrationDocument reg, String cacheType) throws RegisterException {
        String cacheRegKey = "registration_" + reg.getDoctorId().concat("_").concat(reg.getRegisterDate());
        Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP_60);
        Cache.ValueWrapper valueWrapper = tempCache.get(cacheRegKey);
        if (valueWrapper != null && valueWrapper.get() != null) {
            if (cacheType.equals("1")) {
                String patientId = (String) valueWrapper.get();
                if (StringUtil.isEmpty(patientId) || !reg.getPatientId().equals(patientId)) {
                    throw new RegisterException(CenterFunctionUtils.REG_IS_ABSENCE_ERROR);
                }
            } else {
                tempCache.evict(cacheRegKey);
            }
        } else {
            tempCache.put(cacheRegKey, reg.getPatientId());
        }

    }
}
