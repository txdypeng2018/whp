package com.proper.enterprise.isj.proxy.business.his;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DateContext;
import com.proper.enterprise.isj.context.DistrictIdContext;
import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.context.SubjectIdContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.function.schedule.CheckDoctorRegIsValidFunciton;
import com.proper.enterprise.isj.function.schedule.FetchDoctorDeptNonDistrictSetFunction;
import com.proper.enterprise.isj.function.schedule.FetchDoctorMistakeDateFunction;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.document.DoctorScheduleDocument;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.support.function.FunctionUtils;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.RegInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.TimeRegInfo;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegTime;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HisFindDoctorScheduleByDateBusiness<M extends DoctorIdContext<List<DoctorScheduleDocument>>
    & DistrictIdContext<List<DoctorScheduleDocument>>
    & SubjectIdContext<List<DoctorScheduleDocument>>
    & DateContext<List<DoctorScheduleDocument>>
    & ModifiedResultBusinessContext<List<DoctorScheduleDocument>>
>
        implements IBusiness<List<DoctorScheduleDocument>, M> {
    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;
    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;
    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    FetchDoctorMistakeDateFunction fetchDoctorMistakeDateFunction;
    @Autowired
    FetchDoctorDeptNonDistrictSetFunction fetchDoctorDeptNonDistrictSetFunction;

    @Autowired
    CheckDoctorRegIsValidFunciton checkDoctorRegIsValidFunciton;

    @Override
    public void process(M ctx) throws Exception {
        String districtId = ctx.getDistrictId();
        String subjectId = ctx.getSubjectId();
        String doctorId = ctx.getDoctorId();
        String date = ctx.getDate();
        // 获得院区
        List<SubjectDocument> districtList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.CHILD.getCode())).get(String.valueOf(0));
        Map<String, SubjectDocument> districtMap = new HashMap<>();
        for (SubjectDocument dis : districtList) {
            districtMap.put(dis.getId(), dis);
        }
        Date sDate;
        Date eDate;
        Calendar cal = Calendar.getInstance();
        if (StringUtil.isNotEmpty(date)) {
            sDate = DateUtil.toDate(date);
            eDate = DateUtil.toDate(date);
        } else {
            sDate = new Date();
            cal.setTime(sDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            sDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, CenterFunctionUtils.SCHEDULING_MAXADD_DAY);
            eDate = cal.getTime();
        }
        ResModel<RegInfo> allRegInfo = webService4HisInterfaceCacheUtil.getCacheDoctorScheInfoRes(doctorId,
                DateUtil.toDateString(sDate), DateUtil.toDateString(eDate));
        if (allRegInfo.getReturnCode() != ReturnCode.SUCCESS) {
            throw new HisReturnException(allRegInfo.getReturnMsg());
        }
        String disId;
        List<Reg> regList;
        DoctorDocument doc;
        DoctorScheduleDocument docSche;
        ResModel<TimeRegInfo> timeRegInfo;
        List<TimeReg> timeRegList;
        List<RegTime> regTimeList;
        DecimalFormat df = new DecimalFormat("0.00");
        List<DoctorScheduleDocument> docScheList = new ArrayList<>();
        List<RegDoctor> allRegDocList = allRegInfo.getRes().getRegDoctorList();
        // Set<String> regDateSet = null;
        for (RegDoctor regDoctor : allRegDocList) {
            regList = regDoctor.getRegList();
            if (regList == null) {
                continue;
            }
            doc = webServiceCacheUtil.getCacheDoctorDocument().get(regDoctor.getDoctorId());
            Set<String> regDateMoreSet = FunctionUtils.invoke(fetchDoctorMistakeDateFunction, regDoctor, regList);
            FunctionUtils.invoke(fetchDoctorDeptNonDistrictSetFunction, regDoctor, regList);
            for (Reg reg : regList) {
                docSche = new DoctorScheduleDocument();
                docSche.setDate(DateUtil.toDateString(reg.getRegDate()));
                disId = CenterFunctionUtils.convertHisDisId2SubjectId(reg.getRegDistrict());
                if (FunctionUtils.invoke(checkDoctorRegIsValidFunciton, districtId, subjectId, null, null,
                        regDateMoreSet, reg, disId, false, districtMap)) {
                    continue;
                }
                docSche.setDistrictId(disId);
                if (StringUtil.isNotEmpty(docSche.getDistrictId())) {
                    docSche.setDistrict(districtMap.get(docSche.getDistrictId()).getName());
                } else {
                    docSche.setDistrict("");
                }
                docSche.setDoctorId(regDoctor.getDoctorId());
                docSche.setName(regDoctor.getName());
                docSche.setSexCode(doc.getSexCode());
                docSche.setDeptId(reg.getRegDeptcode());
                docSche.setDept(reg.getRegDeptname());
                timeRegInfo = webService4HisInterfaceCacheUtil.getCacheDoctorTimeRegInfoRes(regDoctor.getDoctorId(),
                        DateUtil.toDateString(reg.getRegDate()));
                if (timeRegInfo == null) {
                    continue;
                }
                timeRegList = timeRegInfo.getRes().getTimeRegList();
                for (TimeReg tempReg : timeRegList) {
                    docSche.setTotal(docSche.getTotal() + tempReg.getTotal());
                    docSche.setOverCount(docSche.getOverCount() + tempReg.getOverCount());
                }
                if (docSche.getTotal() == 0) {
                    continue;
                }
                if (docSche.getTotal() > 99) {
                    docSche.setTotal(99);
                }
                if (docSche.getOverCount() > 99) {
                    docSche.setOverCount(99);
                }
                if (docSche.getOverCount() > docSche.getTotal()) {
                    docSche.setOverCount(docSche.getTotal());
                }
                regTimeList = reg.getRegTimeList();
                if (regTimeList == null || regTimeList.size() == 0) {
                    continue;
                }
                RegTime regTime = regTimeList.get(0);
                docSche.setAmount(df.format((regTime.getRegFee() + regTime.getTreatFee()) / 100));
                docSche.setCategoryCode(regTime.getRegLevel());
                docSche.setCategory(CenterFunctionUtils.getRegLevelNameMap().get(regTime.getRegLevel()));

                StringBuilder title = new StringBuilder();
                title.append(CenterFunctionUtils.getRegLevelNameMap().get(docSche.getCategoryCode()));
                if (StringUtil.isNotEmpty(regDoctor.getJobTitle())) {
                    title.append("(").append(regDoctor.getJobTitle()).append(")");
                }
                docSche.setTitle(title.toString());
                docScheList.add(docSche);
            }
        }
        ctx.setResult(docScheList);
    }

}
