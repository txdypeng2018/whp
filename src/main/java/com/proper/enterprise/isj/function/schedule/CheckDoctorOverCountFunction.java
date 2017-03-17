package com.proper.enterprise.isj.function.schedule;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * old:com.proper.enterprise.isj.proxy.service.impl.ScheduleServiceImpl.checkDoctorOverCount(String, String,
 * ScheDoctorDocument, Reg, List<TimeReg>)
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class CheckDoctorOverCountFunction implements IFunction<Boolean> {

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public Boolean execute(Object... params) throws Exception {
        return checkDoctorOverCount((String) params[0], (String) params[1], (ScheDoctorDocument) params[2],
                (Reg) params[3], (List<TimeReg>) params[4]);
    }

    public static boolean checkDoctorOverCount(String today, String hourMin, ScheDoctorDocument scheDoc, Reg reg,
            List<TimeReg> timeRegList) {
        for (TimeReg tempReg : timeRegList) {
            if (today.equals(DateUtil.toDateString(reg.getRegDate()))
                    && hourMin.compareTo(tempReg.getBeginTime()) >= 0) {
                continue;
            }
            scheDoc.setTotal(scheDoc.getTotal() + tempReg.getTotal());
            scheDoc.setOverCount(scheDoc.getOverCount() + tempReg.getOverCount());
            if (scheDoc.getOverCount() > 0) {
                break;
            }
        }
        return scheDoc.getOverCount() > 0;
    }

}
