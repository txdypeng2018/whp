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
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * com.proper.enterprise.isj.proxy.service.impl.ScheduleServiceImpl.getDoctorMistakeDate(RegDoctor, List<Reg>)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchDoctorMistakeDateFunction implements IFunction<Set<String>> {

    @Autowired
    ScheduleMistakeLogRepository scheduleMistakeLogRepository;

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> execute(Object... params) throws Exception {
        return getDoctorMistakeDate((RegDoctor) params[0], (List<Reg>) params[1]);
    }

    /**
     * 保存并返回医生同一天出现多诊别的记录.
     *
     * @param regDoctor 挂号医生.
     * @param tempRegList 临时列表.
     * @return 多诊别记录.
     */
    public Set<String> getDoctorMistakeDate(RegDoctor regDoctor, List<Reg> tempRegList) {
        Set<String> regDateSet = new HashSet<>();
        Set<String> regDateMoreSet = new HashSet<>();
        List<ScheduleMistakeLogDocument> misList = scheduleMistakeLogRepository.findByDoctorIdAndMistakeCode(
                regDoctor.getDoctorId(), ScheduleMistakeCodeEnum.REGDATE_GT_ONE.toString());
        Set<String> haveMistakeSet = new HashSet<>();
        for (ScheduleMistakeLogDocument mistakeLogDocument : misList) {
            haveMistakeSet.add(mistakeLogDocument.getRegDate());
        }
        for (Reg reg : tempRegList) {
            if (regDateSet.contains(DateUtil.toDateString(reg.getRegDate()))) {
                if (!haveMistakeSet.contains(DateUtil.toDateString(reg.getRegDate()))) {
                    ScheduleMistakeLogDocument mistakeLogDocument = new ScheduleMistakeLogDocument();
                    mistakeLogDocument.setDoctorId(regDoctor.getDoctorId());
                    mistakeLogDocument.setDoctorName(regDoctor.getName());
                    mistakeLogDocument.setRegDate(DateUtil.toDateString(reg.getRegDate()));
                    mistakeLogDocument.setMistakeCode(ScheduleMistakeCodeEnum.REGDATE_GT_ONE.toString());
                    mistakeLogDocument.setMistakeDesc(CenterFunctionUtils.SCHEDULE_MISTAKE_REGDATE_GT_ONE);
                    scheduleMistakeLogRepository.save(mistakeLogDocument);
                }
                regDateMoreSet.add(DateUtil.toDateString(reg.getRegDate()));
            }
            regDateSet.add(DateUtil.toDateString(reg.getRegDate()));
        }
        return regDateMoreSet;
    }

}
