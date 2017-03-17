package com.proper.enterprise.isj.function.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.repository.RegistrationRepository;
import com.proper.enterprise.isj.proxy.service.impl.RegistrationServiceImpl;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.notx.RegistrationServiceNotxImpl.saveCreateRegistrationAndOrder(RegistrationDocument, String)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class SaveCreateRegistrationAndOrderFunction
implements IFunction<RegistrationDocument>, ILoggable{

    @Autowired
    RepositoryFunctionToolkit toolkitx;

    @Autowired
    RegistrationServiceImpl registrationServiceImpl;
    
    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Override
    public RegistrationDocument execute(Object... params) throws Exception {
        return saveCreateRegistrationAndOrder((RegistrationDocument) params[0], (String) params[1]);
    }
    
    public RegistrationDocument saveCreateRegistrationAndOrder(RegistrationDocument reg, String isAppointment)
            throws Exception {
        RegistrationDocument saveReg = null;
        try {
            saveReg = registrationServiceImpl.saveCreateRegistration(reg);
            saveReg = registrationServiceImpl.saveCreateRegistrationAndOrder(saveReg, isAppointment);
            if (isAppointment.equals("1")) {
                webService4HisInterfaceCacheUtil.evictCacheDoctorTimeRegInfoRes(saveReg.getDoctorId(),
                        saveReg.getRegDate());
                toolkitx.executeFunction(SaveOrRemoveRegCacheFunction.class, "1", saveReg.getId(),
                        DateUtil.toDate(saveReg.getCreateTime(), PEPConstants.DEFAULT_TIMESTAMP_FORMAT));
            }
        } catch (Exception e) {
            debug("挂号出现异常", e);
            if (saveReg != null) {
                if (isAppointment.equals("1")) {
                    try {
                        if (saveReg.getRegistrationOrderHis() != null
                                && StringUtil.isNotEmpty(saveReg.getRegistrationOrderHis().getHospOrderId())) {
                            registrationServiceImpl.saveCancelRegistrationImpl(saveReg.getId(),
                                    OrderCancelTypeEnum.CANCEL_PLATFORM_ERR);
                        }
                    } catch (Exception e2) {
                        debug("RegistrationServiceNotxImpl.saveCreateRegistrationAndOrder[Exception]:", e2);
                        debug(e2.getCause().getMessage());
                    }
                }
                if (StringUtil.isNotEmpty(saveReg.getId())) {
                    toolkitx.executeRepositoryFunction(RegistrationRepository.class, "delete", saveReg.getId());
                }
            }
            throw e;
        }
        return saveReg;
    }

}
