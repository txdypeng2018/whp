package com.proper.enterprise.isj.function.registration;

import com.proper.enterprise.isj.exception.HisLinkException;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.exception.RegisterException;
import com.proper.enterprise.isj.function.schedule.SaveOrRemoveCacheRegKeyInScheduleFunction;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

@Service
public class CreateRegistrationAndReturnOrderFunction  implements IFunction<Object>, ILoggable {

    @Autowired
    RegistrationService registrationService;
    
    @Autowired
    SaveOrRemoveCacheRegKeyInScheduleFunction saveOrRemoveCacheRegKeyFunction;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public Object execute(Object... params) throws Exception {
        return createRegistrationAndReturnOrder((RegistrationDocument) params[0]);
    }
    
    private Map<String, String> createRegistrationAndReturnOrder(RegistrationDocument reg) throws RegisterException, HisLinkException, HisReturnException {
        try {
            RegistrationDocument saveReg = registrationService.saveCreateRegistrationAndOrder(reg, reg.getIsAppointment());
            FunctionUtils.invoke(saveOrRemoveCacheRegKeyFunction, reg, "0");
            if (saveReg == null) {
                throw new RegisterException(CenterFunctionUtils.ORDER_SAVE_ERR);
            }
            Map<String, String> map = new HashMap<>();
            map.put("orderNum", saveReg.getOrderNum());
            map.put("registrationId", saveReg.getId());
            return map;
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            FunctionUtils.invoke(saveOrRemoveCacheRegKeyFunction, reg, "0");
            throw new HisLinkException(HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            debug("HIS接口返回错误", e);
            FunctionUtils.invoke(saveOrRemoveCacheRegKeyFunction, reg, "0");
            throw new HisReturnException(e.getMessage());
        } catch (RegisterException e) {
            debug("接口内异常", e);
            FunctionUtils.invoke(saveOrRemoveCacheRegKeyFunction, reg, "0");
            throw new RegisterException(e.getMessage(), e);
        } catch (Exception e) {
            debug("系统错误", e);
            FunctionUtils.invoke(saveOrRemoveCacheRegKeyFunction, reg, "0");
            throw new RegisterException(APP_SYSTEM_ERR);
        }
    }
    
}
