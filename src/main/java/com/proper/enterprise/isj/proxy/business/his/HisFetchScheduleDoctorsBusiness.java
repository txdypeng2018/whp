package com.proper.enterprise.isj.proxy.business.his;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DeptIdContext;
import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.EndDateObjContext;
import com.proper.enterprise.isj.context.IsAppointmentBooleanContext;
import com.proper.enterprise.isj.context.MajorContext;
import com.proper.enterprise.isj.context.MaxScheDoctorIndexContext;
import com.proper.enterprise.isj.context.StartDateObjContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.function.schedule.CheckDoctorOverCountFunction;
import com.proper.enterprise.isj.function.schedule.CheckDoctorRegIsValidFunciton;
import com.proper.enterprise.isj.function.schedule.FetchDoctorDeptNonDistrictSetFunction;
import com.proper.enterprise.isj.function.schedule.FetchDoctorMistakeDateFunction;
import com.proper.enterprise.isj.function.schedule.HisFetchScheDoctorListFunction;
import com.proper.enterprise.isj.function.schedule.NeedToFilterFunction;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.RegInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.TimeRegInfo;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.DateUtil;

@Service
public class HisFetchScheduleDoctorsBusiness<M extends DistrictIdContext<List<ScheDoctorDocument>> & DeptIdContext<List<ScheDoctorDocument>> & MajorContext<List<ScheDoctorDocument>> & StartDateObjContext<List<ScheDoctorDocument>> & EndDateObjContext<List<ScheDoctorDocument>> & IsAppointmentBooleanContext<List<ScheDoctorDocument>> & MaxScheDoctorIndexContext<List<ScheDoctorDocument>> & ModifiedResultBusinessContext<List<ScheDoctorDocument>>>
        implements IBusiness<List<ScheDoctorDocument>, M>, ILoggable {

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @SuppressWarnings("rawtypes")
    @Autowired
    HisFetchScheDoctorListFunction fetchScheDoctorListFunction;

    @Autowired
    NeedToFilterFunction needToFilterFunction;

    @Autowired
    FetchDoctorMistakeDateFunction fetchDoctorMistakeDateFunction;

    @Autowired
    FetchDoctorDeptNonDistrictSetFunction fetchDoctorDeptNonDistrictSetFunction;

    @Autowired
    CheckDoctorOverCountFunction checkDoctorOverCountFunction;

    @Autowired
    CheckDoctorRegIsValidFunciton checkDoctorRegIsValidFunciton;

    @SuppressWarnings("unchecked")
    @Override
    public void process(M ctx) throws Exception {

        String districtId = ctx.getDistrictId();
        String deptId = ctx.getDeptId();
        String major = ctx.getMajor();
        Date startDate = ctx.getStartDateObj();
        Date endDate = ctx.getEndDateObj();
        boolean isAppointment = ctx.getIsAppointmentBooleanValue();
        int maxScheDoctorIndex = ctx.getMaxScheDoctorIndex();
        // 获得院区
        List<SubjectDocument> districtList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.CHILD.getCode())).get(String.valueOf(0));
        Map<String, SubjectDocument> districtMap = new HashMap<>();
        for (SubjectDocument dis : districtList) {
            districtMap.put(dis.getId(), dis);
        }
        List<DoctorDocument> scheDoctorList = (List<DoctorDocument>) FunctionUtils.invoke(fetchScheDoctorListFunction,
                deptId, major);
        String today = DateUtil.toDateString(new Date());
        String hourMin = DateUtil.toString(new Date(), "HH:mm");
        List<ScheDoctorDocument> docList = new ArrayList<>();
        ScheDoctorDocument scheDoc;
        List<Reg> tempRegList;
        for (DoctorDocument doctor : scheDoctorList) {
            ResModel<RegInfo> allRegInfo = webService4HisInterfaceCacheUtil.getCacheDoctorScheInfoRes(doctor.getId(),
                    DateUtil.toDateString(startDate), DateUtil.toDateString(endDate));
            if (allRegInfo.getReturnCode() != ReturnCode.SUCCESS) {
                debug("HIS返回的数据出错,returnCode:" + allRegInfo.getReturnCode() + ",错误对象:" + RegInfo.class.getName());
                throw new HisReturnException(allRegInfo.getReturnMsg());
            }
            List<RegDoctor> allRegDocList = allRegInfo.getRes().getRegDoctorList();
            if (allRegDocList == null || allRegDocList.size() == 0) {
                continue;
            }
            RegDoctor regDoctor = allRegDocList.get(0);
            if (FunctionUtils.invoke(needToFilterFunction, isAppointment, regDoctor)) {
                continue;
            }
            tempRegList = regDoctor.getRegList();
            if (tempRegList == null || tempRegList.size() == 0) {
                continue;
            }
            scheDoc = new ScheDoctorDocument();
            BeanUtils.copyProperties(doctor, scheDoc);
            boolean timeRegFlag = false;
            Set<String> regDateMoreSet = FunctionUtils.invoke(fetchDoctorMistakeDateFunction, regDoctor, tempRegList);
            FunctionUtils.invoke(fetchDoctorDeptNonDistrictSetFunction, regDoctor, tempRegList);
            for (Reg reg : tempRegList) {
                scheDoc.setDeptId(reg.getRegDeptcode());
                scheDoc.setDept(reg.getRegDeptname());
                String disId = CenterFunctionUtils.convertHisDisId2SubjectId(reg.getRegDistrict());
                if (FunctionUtils.invoke(checkDoctorRegIsValidFunciton, districtId, deptId, docList, scheDoc,
                        regDateMoreSet, reg, disId, isAppointment, districtMap)) {
                    continue;
                }

                ResModel<TimeRegInfo> timeRegInfo = webService4HisInterfaceCacheUtil
                        .getCacheDoctorTimeRegInfoRes(regDoctor.getDoctorId(), DateUtil.toDateString(reg.getRegDate()));
                if (timeRegInfo.getReturnCode() != ReturnCode.SUCCESS) {
                    debug("HIS返回的数据出错,returnCode:" + timeRegInfo.getReturnCode() + ",错误对象:"
                            + TimeRegInfo.class.getName());
                    continue;
                }
                List<TimeReg> timeRegList = timeRegInfo.getRes().getTimeRegList();
                if (timeRegList.size() > 0) {
                    timeRegFlag = true;
                }
                if (FunctionUtils.invoke(checkDoctorOverCountFunction, today, hourMin, scheDoc, reg, timeRegList)) {
                    break;
                }
            }
            if (timeRegFlag) {
                docList.add(scheDoc);
                if (docList.size() == maxScheDoctorIndex) {
                    break;
                }
            }
        }
        ctx.setResult(docList);

    }

}
