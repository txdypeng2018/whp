package com.proper.enterprise.isj.proxy.business.his;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DateContext;
import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.document.doctor.RegisterDoctorDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.RegInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegTime;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class HisFindDoctorScheduleByTimeBusiness<M extends DoctorIdContext<RegisterDoctorDocument>
    & DateContext<RegisterDoctorDocument> & ModifiedResultBusinessContext<RegisterDoctorDocument>>
        implements IBusiness<RegisterDoctorDocument, M> {

    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;
    
    
    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;
    

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;
    
    @Override
    public void process(M ctx) throws Exception {
        String doctorId = ctx.getDoctorId();
        String date = ctx.getDate();
        Date apptDate = DateUtil.toDate(date, "yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(apptDate);
        // 获得院区
        List<SubjectDocument> districtList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.CHILD.getCode())).get(String.valueOf(0));
        Map<String, SubjectDocument> districtMap = new HashMap<>();
        for (SubjectDocument dis : districtList) {
            districtMap.put(dis.getId(), dis);
        }
        // String hosId = CenterFunctionUtils.getHosId();
        RegisterDoctorDocument regDoc = null;
        DecimalFormat df = new DecimalFormat("0.00");
        ResModel<RegInfo> regInfo = webService4HisInterfaceCacheUtil.getCacheDoctorScheInfoRes(doctorId,
                DateUtil.toDateString(cal.getTime()), DateUtil.toDateString(cal.getTime()));
        Set<String> doctorIdSet = new HashSet<>();
        if (regInfo.getReturnCode() == ReturnCode.SUCCESS) {
            List<RegDoctor> regDocList = regInfo.getRes().getRegDoctorList();
            if (regDocList != null) {
                for (RegDoctor regDoctor : regDocList) {
                    if (doctorIdSet.contains(regDoctor.getDoctorId())) {
                        continue;
                    }
                    doctorIdSet.add(regDoctor.getDoctorId());
                    regDoc = new RegisterDoctorDocument();
                    DoctorDocument doc = webServiceCacheUtil.getCacheDoctorDocument().get(regDoctor.getDoctorId());
                    BeanUtils.copyProperties(doc, regDoc);
                    regDoc.setDeptId(regInfo.getRes().getDeptId());
                    regDoc.setDept(regInfo.getRes().getDeptName());
                    String disId = CenterFunctionUtils.convertHisDisId2SubjectId(regInfo.getRes().getDistrict());

                    if (StringUtil.isEmpty(disId)) {
                        continue;
                    }
                    regDoc.setDistrictId(disId);
                    if (StringUtil.isNotEmpty(regDoc.getDistrictId())) {
                        regDoc.setDistrict(districtMap.get(disId).getName());
                    }
                    List<Reg> regList = regDoctor.getRegList();
                    if (regList != null) {
                        for (Reg reg : regList) {
                            List<RegTime> regTimeList = reg.getRegTimeList();
                            if (regTimeList != null && regTimeList.size() > 0) {
                                RegTime regTime = regTimeList.get(0);
                                regDoc.setClinicCategoryCode(regTime.getRegLevel());
                                regDoc.setAmount(df.format((regTime.getRegFee() + regTime.getTreatFee()) / 100));
                                StringBuilder title = new StringBuilder();
                                title.append(
                                        CenterFunctionUtils.getRegLevelNameMap().get(regDoc.getClinicCategoryCode()));
                                if (StringUtil.isNotEmpty(regDoctor.getJobTitle())) {
                                    title.append("(").append(regDoctor.getJobTitle()).append(")");
                                }
                                regDoc.setTitle(title.toString());
                            }
                        }
                    }

                }
            }
        } else {
            throw new HisReturnException(regInfo.getReturnMsg());
        }

        ctx.setResult(regDoc);

    }

}
