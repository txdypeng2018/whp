package com.proper.enterprise.isj.proxy.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DateContext;
import com.proper.enterprise.isj.context.DateObjContext;
import com.proper.enterprise.isj.context.DeptIdContext;
import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.context.EndDateObjContext;
import com.proper.enterprise.isj.context.IsAppointmentBooleanContext;
import com.proper.enterprise.isj.context.MajorContext;
import com.proper.enterprise.isj.context.MaxScheDoctorIndexContext;
import com.proper.enterprise.isj.context.StartDateObjContext;
import com.proper.enterprise.isj.context.SubjectIdContext;
import com.proper.enterprise.isj.proxy.business.his.HisFetchScheduleDoctorsBusiness;
import com.proper.enterprise.isj.proxy.business.his.HisFindDoctorScheduleByDateBusiness;
import com.proper.enterprise.isj.proxy.business.his.HisFindDoctorScheduleByTimeBusiness;
import com.proper.enterprise.isj.proxy.business.his.HisFindDoctorTimeRegListBusiness;
import com.proper.enterprise.isj.proxy.document.DoctorScheduleDocument;
import com.proper.enterprise.isj.proxy.document.TimeRegDocument;
import com.proper.enterprise.isj.proxy.document.doctor.RegisterDoctorDocument;
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.isj.proxy.service.ScheduleService;
import com.proper.enterprise.isj.support.service.AbstractService;

/**
 * 排班服务.
 * Created by think on 2016/8/19 0019.
 */
@Service
public class ScheduleServiceImpl extends AbstractService implements ScheduleService {

    @Override
    public List<ScheDoctorDocument> getScheduleDoctors(String districtId, String deptId, String major, Date startDate, Date endDate, boolean isAppointment, int maxScheDoctorIndex) throws Exception {
        return toolkit.execute(HisFetchScheduleDoctorsBusiness.class, (c) -> {
            ((DistrictIdContext<?>) c).setDistrictId(districtId);
            ((DeptIdContext<?>) c).setDeptId(deptId);
            ((MajorContext<?>) c).setMajor(major);
            ((StartDateObjContext<?>) c).setStartDateObj(startDate);
            ((EndDateObjContext<?>) c).setEndDateObj(endDate);
            ((IsAppointmentBooleanContext<?>) c).setIsAppointmentBooleanValue(isAppointment);
            ((MaxScheDoctorIndexContext<?>) c).setMaxScheDoctorIndex(maxScheDoctorIndex);
        });
    }

    @Override
    public RegisterDoctorDocument findDoctorScheduleByTime(String doctorId, String date) throws Exception {
        return toolkit.execute(HisFindDoctorScheduleByTimeBusiness.class, (c) -> {
            ((DoctorIdContext<?>) c).setDoctorId(doctorId);
            ((DateContext<?>) c).setDate(date);
        });
    }

    @Override
    public List<DoctorScheduleDocument> findDoctorScheduleByDate(String doctorId, String districtId, String subjectId, String date) throws Exception {
        return toolkit.execute(HisFindDoctorScheduleByDateBusiness.class, (c) -> {
            ((DistrictIdContext<?>) c).setDistrictId(districtId);
            ((DoctorIdContext<?>) c).setDoctorId(doctorId);
            ((SubjectIdContext<?>) c).setSubjectId(subjectId);
            ((DateContext<?>) c).setDate(date);
        });
    }

    @Override
    public List<TimeRegDocument> findDoctorTimeRegList(String doctorId, Date date) throws Exception {
        return toolkit.execute(HisFindDoctorTimeRegListBusiness.class, (c) -> {
            ((DoctorIdContext<?>) c).setDoctorId(doctorId);
            ((DateObjContext<?>) c).setDateObj(date);
        });
        
    }

}
