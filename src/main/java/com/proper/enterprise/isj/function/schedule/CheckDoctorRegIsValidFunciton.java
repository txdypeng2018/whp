package com.proper.enterprise.isj.function.schedule;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.impl.ScheduleServiceImpl.checkDoctorRegIsValid(String, String, List<ScheDoctorDocument>, ScheDoctorDocument, Set<String>, Reg, String, boolean, Map<String, SubjectDocument>)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class CheckDoctorRegIsValidFunciton implements 
IFunction<Boolean>, ILoggable {

    @Autowired
    RegistrationService registrationService;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public Boolean execute(Object... params) throws Exception {
        return checkDoctorRegIsValid((String) params[0], (String) params[1], (List<ScheDoctorDocument>) params[2],
                (ScheDoctorDocument) params[3], (Set<String>) params[4], (Reg) params[5], (String) params[6], 
                (boolean) params[7], (Map<String, SubjectDocument>) params[8]);
    }


    /**
     * 校验医生排班是否合法.
     *
     * @param districtId     院区ID.
     * @param deptId         科室ID.
     * @param docList        医生列表.
     * @param scheDoc        排班信息.
     * @param regDateMoreSet 挂号日期.
     * @param reg            挂号信息.
     * @param disId          挂号院区.
     * @return 检查结果.
     */
    public static boolean checkDoctorRegIsValid(String districtId, String deptId, List<ScheDoctorDocument> docList, ScheDoctorDocument scheDoc, Set<String> regDateMoreSet, Reg reg, String disId, boolean isAppointment, Map<String, SubjectDocument> districtMap) {
        // 过滤医生排班没返回出诊院区人员
        if (StringUtil.isEmpty(disId)) {
            LOGGER.debug("医生排班没返回出诊院区");
            return true;
        }
        // 过滤一天多个挂号级别人员
        if (regDateMoreSet.contains(DateUtil.toDateString(reg.getRegDate()))) {
            LOGGER.debug("医生一天多个挂号级别");
            return true;
        }
        // 过滤院区科室与his的出诊院区不同的人员
        if (StringUtil.isNotEmpty(deptId) && StringUtil.isNotEmpty(districtId)) {
            if (!disId.equals(districtId)) {
                LOGGER.debug("医生院区科室与his的出诊院区不同");
                return true;
            }
        }
        // 预约挂号,全局搜索将院区赋值为空
        if (isAppointment && StringUtil.isEmpty(deptId)) {
            disId = "";
        }
        if (scheDoc != null) {
            scheDoc.setDistrictId(disId);
            if (StringUtil.isNotEmpty(scheDoc.getDistrictId())) {
                scheDoc.setDistrict(districtMap.get(scheDoc.getDistrictId()).getName());
            } else {
                scheDoc.setDistrict("");
            }
            // 将没有上下午挂号级别的医生,改为暂停挂号
            if (reg.getRegTimeList() == null || reg.getRegTimeList().size() == 0) {
                scheDoc.setUnallowed(CenterFunctionUtils.REG_TEMP_STOP);
                docList.add(scheDoc);
                return true;
            }
        }

        return false;
    }

}
