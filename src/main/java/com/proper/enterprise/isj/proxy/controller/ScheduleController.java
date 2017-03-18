package com.proper.enterprise.isj.proxy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.context.DateContext;
import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.context.EndDateContext;
import com.proper.enterprise.isj.context.IsAppointmentContext;
import com.proper.enterprise.isj.context.MajorContext;
import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.StartDateContext;
import com.proper.enterprise.isj.context.SubjectIdContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.schedule.FetchDoctorDateBusiness;
import com.proper.enterprise.isj.proxy.business.schedule.FetchScheduleDoctorsBusiness;
import com.proper.enterprise.isj.proxy.business.schedule.ScheduleGetTimesBusiness;
import com.proper.enterprise.isj.proxy.document.DoctorScheduleDocument;
import com.proper.enterprise.isj.proxy.document.TimeRegDocument;
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

/**
 * Created by think on 2016/8/16 0016. 指定医生排班时间列表（只返回有号日期）
 */
@RestController
@RequestMapping(path = "/schedule")
@AuthcIgnore
public class ScheduleController extends IHosBaseController {

    /**
     * 指定医生排班时间列表（只返回有号日期）.
     *
     * @param doctorId 医生id.
     * @param districtId 院区id.
     * @param date 日期.
     * @param isAppointment 1:预约,0:当日
     * @param subjectId 科室id.
     * @return 返回结果.
     */
    @RequestMapping(path = "/dates", method = RequestMethod.GET)
    public ResponseEntity<List<DoctorScheduleDocument>> getDoctorDate(@RequestParam String doctorId, String districtId,
            String subjectId, String date, @RequestParam String isAppointment) {
        return responseOfGet(toolkit.execute(FetchDoctorDateBusiness.class, (c) -> {
            ((DoctorIdContext<?>) c).setDoctorId(doctorId);
            ((DistrictIdContext<?>) c).setDistrictId(districtId);
            ((SubjectIdContext<?>) c).setSubjectId(subjectId);
            ((DateContext<?>) c).setDate(date);
            ((IsAppointmentContext<?>) c).setIsAppointment(isAppointment);
        }));
    }

    /**
     * 取得排班医生列表（只返回有号医生）
     *
     * @param districtId 院区Id.
     * @param subjectId 学科Id.
     * @param major 专业/专长.
     * @param startDate 排班开始时间.
     * @param endDate 排班结束时间.
     * @param isAppointment 1:预约,0:当日.
     * @return 排班医生列表（只返回有号医生）.
     */
    @RequestMapping(path = "/doctors", method = RequestMethod.GET)
    public ResponseEntity<List<ScheDoctorDocument>> getScheduleDoctors(String districtId, String subjectId,
            String major, @RequestParam String startDate, String endDate, Integer pageNo,
            @RequestParam String isAppointment) {
        return responseOfGet(toolkit.execute(FetchScheduleDoctorsBusiness.class, (c) -> {
            ((DistrictIdContext<?>) c).setDistrictId(districtId);
            ((SubjectIdContext<?>) c).setSubjectId(subjectId);
            ((MajorContext<?>) c).setMajor(major);
            ((StartDateContext<?>) c).setStartDate(startDate);
            ((EndDateContext<?>) c).setEndDate(endDate);
            ((PageNoContext<?>) c).setPageNo(pageNo);
            ((IsAppointmentContext<?>) c).setIsAppointment(isAppointment);
        }));
    }

    /**
     * 指定日期医生一天出诊时间列表（只返回有号时间）
     *
     * @param date 日期.
     * @param doctorId 医生ID.
     * @return 返回应答.
     */
    @RequestMapping(path = "/times", method = RequestMethod.GET)
    public ResponseEntity<List<TimeRegDocument>> getTimes(@RequestParam String date, @RequestParam String doctorId,
            @RequestParam String isAppointment) {
        return responseOfGet(toolkit.execute(ScheduleGetTimesBusiness.class, (c) -> {
            ((DateContext<?>) c).setDate(date);
            ((DoctorIdContext<?>) c).setDoctorId(doctorId);
            ((IsAppointmentContext<?>) c).setIsAppointment(isAppointment);
        }));
    }
}
