package com.proper.enterprise.isj.function.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.api.IFunction;

@Service
public class SaveOrRemoveCacheRegKeyInScheduleFunction implements IFunction<Object> {

    @Autowired
    RegistrationService registrationService;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public Object execute(Object... params) throws Exception {
        saveOrRemoveCacheRegKey((RegistrationDocument) params[0], (String) params[1]);
        return null;
    }

    private final static Object REG_LOCK = new Object();

    /**
     * 将号点锁定,或释放,减少对his接口的访问
     *
     * @param reg 挂号单信息, 保存的Key值:registion_医生Id_挂号日期+时间点
     * @param cacheType 1:保存缓存,如果有则抛异常,0:清缓存
     * @throws RegisterException 异常.
     */
    public void saveOrRemoveCacheRegKey(RegistrationDocument reg, String cacheType) throws RegisterException {
        synchronized (REG_LOCK) {
            registrationService.saveOrRemoveCacheRegKey(reg, cacheType);
        }
    }
}
