package com.proper.enterprise.isj.function.registration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.saveOrRemoveRegCache(String, String, Date)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class SaveOrRemoveRegCacheFunction implements IFunction<Object> {

    @Autowired
    CacheManager cacheManager;

    /**
     * 清除或保存30分钟倒计时的挂号单.
     *
     * @param type 1:添加,0:清除.
     * @param regId 挂号ID.
     * @param regCreateTime 挂号时间.
     */
    @SuppressWarnings("unchecked")
    public void saveOrRemoveRegCache(String type, String regId, Date regCreateTime) {
        Cache tempCache = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP);
        Cache.ValueWrapper valueWrapper = tempCache.get(CenterFunctionUtils.CACHE_KEY_REGITRATION_SCHEDULER_TASK);
        Map<String, Date> regMap = new HashMap<>();
        if (valueWrapper != null && valueWrapper.get() != null) {
            // noinspection unchecked
            regMap = (Map<String, Date>) valueWrapper.get();
        }
        if (type.equals("1")) {
            regMap.put(regId, regCreateTime);
        } else {
            regMap.remove(regId);
        }
        tempCache.put(CenterFunctionUtils.CACHE_KEY_REGITRATION_SCHEDULER_TASK, regMap);
    }

    @Override
    public Object execute(Object... params) throws Exception {
        saveOrRemoveRegCache((String) params[0], (String) params[1], (Date) params[2]);
        return null;
    }
}
