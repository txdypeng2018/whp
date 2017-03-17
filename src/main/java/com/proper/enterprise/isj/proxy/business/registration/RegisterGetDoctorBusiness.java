package com.proper.enterprise.isj.proxy.business.registration;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DateContext;
import com.proper.enterprise.isj.context.IdContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.doctor.RegisterDoctorDocument;
import com.proper.enterprise.isj.proxy.service.ScheduleService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

@Service
public class RegisterGetDoctorBusiness<T, M extends DateContext<RegisterDoctorDocument> & IdContext<RegisterDoctorDocument> & ModifiedResultBusinessContext<RegisterDoctorDocument>>
        implements IBusiness<RegisterDoctorDocument, M>, ILoggable {
    @Autowired
    ScheduleService scheduleService;

    @Override
    public void process(M ctx) throws Exception {
        String id = ctx.getId();
        String date = ctx.getDate();
        RegisterDoctorDocument scheList;
        try {
            scheList = scheduleService.findDoctorScheduleByTime(id, date);
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            throw new RuntimeException(HIS_DATALINK_ERR, e);
        } catch (HisReturnException e) {
            debug("HIS接口返回错误", e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            debug("系统错误", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(scheList);
    }
}
