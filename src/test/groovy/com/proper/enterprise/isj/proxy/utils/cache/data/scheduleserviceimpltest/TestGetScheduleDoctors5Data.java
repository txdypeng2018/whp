package com.proper.enterprise.isj.proxy.utils.cache.data.scheduleserviceimpltest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode;
import com.proper.enterprise.isj.webservices.model.res.RegInfo;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.TimeRegInfo;
import com.proper.enterprise.isj.webservices.model.res.deptinfo.Dept;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * Created by think on 2016/10/28 0028.
 */
@Component
public class TestGetScheduleDoctors5Data {
    @Autowired
    CacheManager cacheManager;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    public void cacheData(Date startDate, Date endDate) throws Exception {
        Cache cache = cacheManager.getCache("pep-temp");
        Cache cache120 = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP_120);
        List<Dept> deptList = getDepts();
        cache.put("his_deptinfo", deptList);
        cache.put("subjectDocument", webServiceDataSecondCacheUtil.cacheSubjectDocument());
        ResModel<RegInfo> resModel = getRegDoctors();
        for (RegDoctor doc : resModel.getRes().getRegDoctorList()) {
            for (Reg tempReg : doc.getRegList()) {
                cache120.put("doctorSche_" + doc.getDoctorId() + DateUtil.toDateString(startDate)
                        + DateUtil.toDateString(endDate), resModel);
                cache120.put("doctorTimeReg_" + doc.getDoctorId() + DateUtil.toDateString(tempReg.getRegDate()),
                        this.getTimeRegInfoResModel(doc.getDoctorId(), DateUtil.toDateString(tempReg.getRegDate())));
            }
        }
    }

    private ResModel<RegInfo> getRegDoctors() {
        RegInfo regInfo = new RegInfo();
        regInfo.setHosId(CenterFunctionUtils.getHosId());
        List<RegDoctor> regDoctorList = new ArrayList<>();
        RegDoctor regDoctor = new RegDoctor();
        regDoctor.setDoctorId("011193");
        regDoctor.setJobTitle("主治医师");
        regDoctor.setName("金冶");
        List<Reg> regList = new ArrayList<>();
        Reg reg = new Reg();
        reg.setRegDate("2016-10-30");
        reg.setRegDeptcode("0010");
        reg.setRegDeptname("中医科");
        reg.setRegDistrict("1");
        regList.add(reg);
        regDoctor.setRegList(regList);
        regDoctorList.add(regDoctor);
        regInfo.setRegDoctorList(regDoctorList);
        ResModel<RegInfo> resModel = new ResModel<>();
        resModel.setReturnCode(ReturnCode.SUCCESS.getCode());
        resModel.setRes(regInfo);
        return resModel;
    }

    private List<Dept> getDepts() {
        List<Dept> deptList = new ArrayList<>();
        Dept dept = new Dept();
        dept.setParentId("0");
        dept.setDeptId("1207");
        dept.setDeptName("南湖");
        dept.setLevel(1);
        deptList.add(dept);
        dept = new Dept();
        dept.setParentId("1207");
        dept.setDeptId("0010");
        dept.setDeptName("中医科");
        dept.setLevel(1);
        deptList.add(dept);
        dept = new Dept();
        dept.setParentId("0010");
        dept.setDeptId("011193");
        dept.setDeptName("金冶");
        dept.setLevel(2);
        deptList.add(dept);
        return deptList;
    }

    public ResModel<TimeRegInfo> getTimeRegInfoResModel(String doctorId, String regDate) {
        ResModel<TimeRegInfo> resModel = new ResModel<>();
        TimeRegInfo timeRegInfo = new TimeRegInfo();
        List<TimeReg> timeRegList = new ArrayList<>();
        TimeReg timeReg = new TimeReg();
        timeReg.setBeginTime("11:11");
        timeRegList.add(timeReg);
        timeRegInfo.setTimeRegList(timeRegList);
        resModel.setRes(timeRegInfo);
        return resModel;
    }
}
