package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.document.DoctorScheduleDocument;
import com.proper.enterprise.isj.proxy.document.TimeRegDocument;
import com.proper.enterprise.isj.proxy.document.doctor.RegisterDoctorDocument;
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;

import java.util.Date;
import java.util.List;

/**
 * 排班信息.
 * Created by think on 2016/8/19 0019.
 */
public interface ScheduleService {

    /**
     * 平台通过调用HIS的该接口获取某医生具体的排班信息.
     * <p>医生ID（DOCTOR_ID）为-1时查询科室ID下所有医生排班。</p>
     * 
     * @param districtId
     *            院区Id.
     *
     * @param deptId
     *            科室ID，HIS系统中科室唯一ID.
     * @param majorId
     *            模糊搜索.
     * @param startDate
     *            排班开始日期，格式：YYYY-MM-DD.
     * @param endDate
     *            排班结束日期，格式：YYYY-MM-DD.
     * @param isAppointment
     *            是否为预约挂号.
     * @return 响应模型及排班信息对象.
     * @throws Exception 异常.
     */
    List<ScheDoctorDocument> getScheduleDoctors(String districtId, String deptId, String majorId, Date startDate,
            Date endDate, boolean isAppointment, int maxScheDoctorIndex) throws Exception;

    /**
     * 按照时间查找医生排班信息.
     * @param doctorId 医生ID.
     * 
     * @param date 日期.
     * 
     * @return 排班信息.
     */
    RegisterDoctorDocument findDoctorScheduleByTime(String doctorId, String date) throws Exception;

    /**
     * 按照时间查找医生排班信息列表.
     * @param doctorId 医生ID.
     * @param districtId 院区ID.
     * @param subjectId 科室ID.
     * @param date 日期.
     * @return 排班信息列表.
     * @throws Exception 异常.
     */
    List<DoctorScheduleDocument> findDoctorScheduleByDate(String doctorId, String districtId, String subjectId, String date)
            throws Exception;

    /**
     * 查找医生被挂号列表.
     * @param doctorId 医生ID.
     * @param date 日期.
     * @return 医生被挂号列表.
     */
    List<TimeRegDocument> findDoctorTimeRegList(String doctorId, Date date) throws Exception;

}
