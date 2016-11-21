package com.proper.enterprise.isj.rule;

import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor;
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SameDayRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(SameDayRule.class);

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    /**
     * 判断医生是否属于某些科室下
     *
     * @param  doctor     医生信息
     * @param  deptCodes  科室编码数组
     * @return true: 属于；false：不属于
     */
    public boolean underDepts(RegDoctor doctor, String... deptCodes) {
        Set<String> ids = new HashSet<>();

        for (String deptCode : deptCodes) {
            ids.addAll(getDoctorIdsUnderDept(deptCode));
        }

        boolean result = ids.contains(doctor.getDoctorId());
        LOGGER.debug("Checking underDepts rule: {} {} under {}",
                doctor.getDoctorId(), result ? "IS" : "NOT", Arrays.toString(deptCodes));
        return result;
    }

    private Set<String> getDoctorIdsUnderDept(String deptCode) {
        Set<String> ids = new HashSet<>();
        try {
            Map<String, Map<String, List<SubjectDocument>>> allInfo =
                    webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument();
            List<SubjectDocument> deptChildren = allInfo.get(String.valueOf(DeptLevel.CHILD.getCode())).get(deptCode);
            Set<String> leafDepts = new HashSet<>();
            if (deptChildren == null) {
                leafDepts.add(deptCode);
            } else {
                leafDepts = extractLeafDeptIds(deptChildren);
            }
            Map<String, List<SubjectDocument>> doctors = allInfo.get(DeptLevel.DOCTOR.getCode() + "");
            for (String leaf : leafDepts) {
                for (SubjectDocument doc : doctors.get(leaf)) {
                    ids.add(doc.getId());
                }
            }
            LOGGER.debug("These doctors ({}) are under {} dept", Arrays.toString(ids.toArray()), deptCode);
        } catch (Exception e) {
            LOGGER.debug("Exception occurs during get doctor ids under {}: {}", deptCode, e.getMessage());
        }
        return ids;
    }

    private Set<String> extractLeafDeptIds(List<SubjectDocument> list) {
        Set<String> ids = new HashSet<>();
        for (SubjectDocument doc : list) {
            if (doc.getSubjects() == null || doc.getSubjects().isEmpty()) {
                ids.add(doc.getId());
            } else {
                ids.addAll(extractLeafDeptIds(doc.getSubjects()));
            }
        }
        return ids;
    }

    /**
     * 判断医生号点是否都属于挂号级别列表内
     * 挂号级别属性仅在号点信息内是真实的（东软 HIS 接口）
     *
     * @param  doctor 医生信息
     * @param  regLvs 挂号级别编码列表
     * @return true：属于；false：不属于
     */
    public boolean regLvIn(RegDoctor doctor, String... regLvs) {
        boolean in = false;

        List<String> regLvList = Arrays.asList(regLvs);
        List<Reg> regList = doctor.getRegList();
        List<RegTime> regTimes;

        for (Reg reg : regList) {
            regTimes = reg.getRegTimeList();
            for (RegTime regTime : regTimes) {
                in = true;
                LOGGER.debug("Checking {} in {} or not", regTime.getRegLevel(), Arrays.toString(regLvs));
                if (!regLvList.contains(regTime.getRegLevel())) {
                    return false;
                }
            }
        }

        LOGGER.debug("{} {} in {}", doctor.getName(), in ? "IS" : "NOT", Arrays.toString(regLvs));
        return in;
    }

}
