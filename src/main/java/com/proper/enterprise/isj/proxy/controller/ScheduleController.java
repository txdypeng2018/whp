package com.proper.enterprise.isj.proxy.controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.DoctorScheduleDocument;
import com.proper.enterprise.isj.proxy.document.TimeRegDocument;
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.isj.proxy.service.ScheduleService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/8/16 0016. 指定医生排班时间列表（只返回有号日期）
 */
@RestController
@RequestMapping(path = "/schedule")
@JWTIgnore
public class ScheduleController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 指定医生排班时间列表（只返回有号日期）
     * 
     * @param doctorId
     *            医生id
     * @param districtId
     *            院区id
     * @param date
     *            日期
     * @param isAppointment
     *            1:预约,0:当日
     * @param subjectId
     *            科室id
     * @return
     */
    @RequestMapping(path = "/dates", method = RequestMethod.GET)
    public ResponseEntity<List<DoctorScheduleDocument>> getDoctorDate(@RequestParam(required = true) String doctorId,
            String districtId, String subjectId, String date, @RequestParam(required = true) String isAppointment) {
        List<DoctorScheduleDocument> scheList = null;
        try {
            if(isAppointment.equals(String.valueOf(0))&&DateUtil.toDate(date).compareTo(new Date())>0){
                return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.USER_APP_TIME_ERR,
                        HttpStatus.BAD_REQUEST);
            }
            scheList = scheduleService.findDoctorScheduleByDate(doctorId, districtId, subjectId, date);
            if (scheList != null) {
                Collections.sort(scheList, new Comparator<DoctorScheduleDocument>() {
                    @Override
                    public int compare(DoctorScheduleDocument o1, DoctorScheduleDocument o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
            }
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS接口返回参数错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HisReturnException e) {
            LOGGER.debug("HIS接口返回错误", e);
            return CenterFunctionUtils.setTextResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.debug("系统错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(scheList);
    }

    /**
     * 取得排班医生列表（只返回有号医生）
     *
     * @param districtId
     *            院区Id
     * @param subjectId
     *            学科Id
     * @param major
     *            专业/专长
     * @param startDate
     *            排班开始时间
     * @param endDate
     *            排班结束时间
     * @param isAppointment
     *            1:预约,0:当日
     * @return 排班医生列表（只返回有号医生）
     */
    @RequestMapping(path = "/doctors", method = RequestMethod.GET)
    public ResponseEntity<List<ScheDoctorDocument>> getScheduleDoctors(String districtId, String subjectId,
            String major, @RequestParam(required = true) String startDate, String endDate,
            Integer pageNo, @RequestParam(required = true) String isAppointment) {
        if (pageNo == null) {
            pageNo = 1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtil.toDate(startDate));
        Date sDate = cal.getTime();
        Date eDate = null;
        if (endDate != null) {
            cal.setTime(DateUtil.toDate(endDate));
            eDate = cal.getTime();
        } else {
            eDate = sDate;
        }
        List<ScheDoctorDocument> docList = null;
        if(StringUtil.isEmpty(subjectId)&&StringUtil.isEmpty(major)){
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.SEARCH_CONDITION_NULL_ERROR,
                    HttpStatus.BAD_REQUEST);
        }
        try {
            boolean isAppoint = "1".equals(isAppointment);
            if(!isAppoint && sDate.compareTo(new Date())>0){
                return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.USER_APP_TIME_ERR,
                        HttpStatus.BAD_REQUEST);
            }
            int fromIndex = (pageNo - 1) * CenterFunctionUtils.DEFAULT_PAGE_NUM;
            int toIndex = fromIndex + CenterFunctionUtils.DEFAULT_PAGE_NUM;
            docList = scheduleService.getScheduleDoctors(districtId, subjectId, major, sDate, eDate, isAppoint, toIndex);
            if (toIndex > docList.size()) {
                toIndex = docList.size();
            }
            if (fromIndex <= docList.size()) {
                docList = docList.subList(fromIndex, toIndex);
            } else {
                docList = new ArrayList<>();
            }
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS接口返回参数错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HisReturnException e) {
            LOGGER.debug("HIS接口返回错误", e);
            return CenterFunctionUtils.setTextResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.debug("系统错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(docList);
    }

    /**
     * 指定日期医生一天出诊时间列表（只返回有号时间）
     *
     * @param date
     *            日期
     * @param doctorId
     *            医生ID
     * @return
     */
    @RequestMapping(path = "/times", method = RequestMethod.GET)
    public ResponseEntity<List<TimeRegDocument>> getTimes(@RequestParam(required = true) String date,
            @RequestParam(required = true) String doctorId, @RequestParam(required = true) String isAppointment) {
        List<TimeRegDocument> timeList = null;
        try {
            if(isAppointment.equals(String.valueOf(0))&&DateUtil.toDate(date).compareTo(new Date())>0){
                return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.USER_APP_TIME_ERR,
                        HttpStatus.BAD_REQUEST);
            }
            timeList = scheduleService.findDoctorTimeRegList(doctorId, DateUtil.toDate(date));
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS接口返回参数错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HisReturnException e) {
            LOGGER.debug("HIS接口返回错误", e);
            return CenterFunctionUtils.setTextResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.debug("系统错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(timeList);
    }
}
