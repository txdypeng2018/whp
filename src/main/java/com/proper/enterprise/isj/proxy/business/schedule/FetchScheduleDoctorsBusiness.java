package com.proper.enterprise.isj.proxy.business.schedule;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.SEARCH_CONDITION_NULL_ERROR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.USER_APP_TIME_ERR;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.EndDateContext;
import com.proper.enterprise.isj.context.IsAppointmentContext;
import com.proper.enterprise.isj.context.MajorContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.StartDateContext;
import com.proper.enterprise.isj.context.SubjectIdContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.isj.proxy.service.ScheduleService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchScheduleDoctorsBusiness<T, M extends DistrictIdContext<Object> & SubjectIdContext<Object> & MajorContext<Object> & StartDateContext<Object> & EndDateContext<Object> & PageNoContext<Object> & IsAppointmentContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    @Autowired
    ScheduleService scheduleService;

    @Override
    public void process(M ctx) throws Exception {
        Integer pageNo = ctx.getPageNo();
        String startDate = ctx.getStartDate();
        String endDate = ctx.getEndDate();
        String subjectId = ctx.getSubjectId();
        String major = ctx.getMajor();
        String isAppointment = ctx.getIsAppointment();
        String districtId = ctx.getDistrictId();

        if (pageNo == null) {
            pageNo = 1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.toDate(startDate));
        Date sDate = cal.getTime();
        Date eDate;
        if (endDate != null) {
            cal.setTime(DateUtil.toDate(endDate));
            eDate = cal.getTime();
        } else {
            eDate = sDate;
        }
        List<ScheDoctorDocument> docList;
        if (StringUtil.isEmpty(subjectId) && StringUtil.isEmpty(major)) {
            throw new RuntimeException(SEARCH_CONDITION_NULL_ERROR);
        }
        try {
            boolean isAppoint = "1".equals(isAppointment);
            if (!isAppoint && sDate.compareTo(new Date()) > 0) {
                throw new RuntimeException(USER_APP_TIME_ERR);
            }
            int fromIndex = (pageNo - 1) * CenterFunctionUtils.DEFAULT_PAGE_NUM;
            int toIndex = fromIndex + CenterFunctionUtils.DEFAULT_PAGE_NUM;
            docList = scheduleService.getScheduleDoctors(districtId, subjectId, major, sDate, eDate, isAppoint,
                    toIndex);
            if (toIndex > docList.size()) {
                toIndex = docList.size();
            }
            if (fromIndex <= docList.size()) {
                docList = docList.subList(fromIndex, toIndex);
            } else {
                docList = new ArrayList<>();
            }
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            throw new RuntimeException(HIS_DATALINK_ERR);
        } catch (HisReturnException e) {
            debug("HIS接口返回错误", e);
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            debug("系统错误", e);
            throw new RuntimeException(APP_SYSTEM_ERR);
        }
        ctx.setResult(docList);
    }
}
