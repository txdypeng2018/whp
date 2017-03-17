package com.proper.enterprise.isj.proxy.business.his;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DateObjContext;
import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.TimeRegDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.TimeRegInfo;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class HisFindDoctorTimeRegListBusiness<T extends Collection<TimeRegDocument>, M extends DoctorIdContext<T> & DateObjContext<T> & ModifiedResultBusinessContext<T>>
        implements IBusiness<T, M> {

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @SuppressWarnings("unchecked")
    @Override
    public void process(M ctx) throws Exception {
        String doctorId = ctx.getDoctorId();
        Date date = ctx.getDateObj();
        ResModel<TimeRegInfo> timeRegInfo = webService4HisInterfaceCacheUtil.getCacheDoctorTimeRegInfoRes(doctorId,
                DateUtil.toDateString(date));
        Calendar calT = Calendar.getInstance();
        Calendar calD = Calendar.getInstance();
        calT.setTime(new Date());
        calD.setTime(date);
        boolean flag = false;
        String hourMin = "";
        if (calT.get(Calendar.YEAR) == calD.get(Calendar.YEAR) && calT.get(Calendar.MONTH) == calD.get(Calendar.MONTH)
                && calT.get(Calendar.DAY_OF_MONTH) == calD.get(Calendar.DAY_OF_MONTH)) {
            flag = true;
            hourMin = DateUtil.toString(calT.getTime(), "HH:mm");
        }
        List<TimeRegDocument> timeRegDocumentList = new ArrayList<>();
        TimeRegDocument timeReg;
        if (timeRegInfo.getReturnCode() == ReturnCode.SUCCESS) {
            List<TimeReg> timeRegList = timeRegInfo.getRes().getTimeRegList();
            if (timeRegList != null) {
                for (TimeReg reg : timeRegList) {
                    if (flag) {
                        if (hourMin.compareTo(reg.getBeginTime()) >= 0) {
                            continue;
                        }
                    }
                    timeReg = new TimeRegDocument();
                    timeReg.setTime(reg.getBeginTime());
                    timeReg.setTotal(reg.getTotal());
                    timeReg.setOverCount(reg.getOverCount());
                    timeRegDocumentList.add(timeReg);
                }
            }
        } else {
            throw new HisReturnException(timeRegInfo.getReturnMsg());
        }
        ctx.setResult((T) timeRegDocumentList);

    }

}
