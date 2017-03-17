package com.proper.enterprise.isj.function.schedule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.ScheduleMistakeLogDocument;
import com.proper.enterprise.isj.proxy.enums.ScheduleMistakeCodeEnum;
import com.proper.enterprise.isj.proxy.repository.ScheduleMistakeLogRepository;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * com.proper.enterprise.isj.proxy.service.impl.ScheduleServiceImpl.getDoctorDeptNonDistrictSet(RegDoctor, List<Reg>)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class FetchDoctorDeptNonDistrictSetFunction implements IFunction<Object> {


    @Autowired
    ScheduleMistakeLogRepository scheduleMistakeLogRepository;
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object... params) throws Exception {
        getDoctorDeptNonDistrictSet((RegDoctor)params[0], (List<Reg>) params[1]);
        return null;
    }
    


    /**
     * 保存并返回医生无院区的的科室Id.
     *
     * @param regDoctor   挂号医生.
     * @param tempRegList 临时列表.
     */
    public void getDoctorDeptNonDistrictSet(RegDoctor regDoctor, List<Reg> tempRegList) {
        Set<String> deptNonDistrictSet = new HashSet<>();
        List<ScheduleMistakeLogDocument> misList = scheduleMistakeLogRepository.findByDoctorIdAndMistakeCode(regDoctor.getDoctorId(), ScheduleMistakeCodeEnum.DEPT_NON_DISTRICT.toString());
        for (ScheduleMistakeLogDocument mistakeLogDocument : misList) {
            deptNonDistrictSet.add(mistakeLogDocument.getDoctorId() + "_" + mistakeLogDocument.getDeptId());
        }
        for (Reg reg : tempRegList) {
            if (StringUtil.isEmpty(reg.getRegDistrict())) {
                if (deptNonDistrictSet.contains(regDoctor.getDoctorId() + "_" + reg.getRegDeptcode())) {
                    continue;
                }
                ScheduleMistakeLogDocument mistakeLogDocument = new ScheduleMistakeLogDocument();
                mistakeLogDocument.setDoctorId(regDoctor.getDoctorId());
                mistakeLogDocument.setDoctorName(regDoctor.getName());
                mistakeLogDocument.setDeptId(reg.getRegDeptcode());
                mistakeLogDocument.setDeptName(reg.getRegDeptname());
                mistakeLogDocument.setMistakeCode(ScheduleMistakeCodeEnum.DEPT_NON_DISTRICT.toString());
                mistakeLogDocument.setMistakeDesc(CenterFunctionUtils.SCHEDULE_MISTAKE_DEPT_NON_DISTRICT);
                scheduleMistakeLogRepository.save(mistakeLogDocument);
                break;
            }
        }
    }

}
