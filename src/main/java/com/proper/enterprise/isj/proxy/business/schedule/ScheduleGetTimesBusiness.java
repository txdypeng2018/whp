package com.proper.enterprise.isj.proxy.business.schedule;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.USER_APP_TIME_ERR;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DateContext;
import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.context.IsAppointmentContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.TimeRegDocument;
import com.proper.enterprise.isj.proxy.service.ScheduleService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class ScheduleGetTimesBusiness<T, M extends DateContext<Object> & DoctorIdContext<Object> & IsAppointmentContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {
    @Autowired
    ScheduleService scheduleService;

    @Override
    public void process(M ctx) throws Exception {
        String isAppointment = ctx.getIsAppointment();
        String date = ctx.getDate();
        String doctorId = ctx.getDoctorId();

        List<TimeRegDocument> timeList = null;
        try {
            if (isAppointment.equals(String.valueOf(0)) && DateUtil.toDate(date).compareTo(new Date()) > 0) {
                throw new RuntimeException(USER_APP_TIME_ERR);
            }
            if (StringUtil.isNotNull(date)) {
                timeList = scheduleService.findDoctorTimeRegList(doctorId, DateUtil.toDate(date));
            }
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
        ctx.setResult(timeList);
    }
}
