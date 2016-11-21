package com.proper.enterprise.isj.proxy.utils.cache;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.DoctorService;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.isj.webservices.model.res.deptinfo.Dept;

/**
 * Created by think on 2016/9/23 0023.
 */
@Component
@CacheConfig(cacheNames = "pep-temp")
public class WebServiceDataSecondCacheUtil {

    private static final String SUBJECT_DISTRICT_MAP_KEY = "'subjectDistrictMap'";

    private static final String SUBJECT_MAP_KEY = "'subjectMap'";

    private static final String SUBJECTDOCUMENT_KEY = "'subjectDocument'";

    private static final String OA2HISDOCINFO_KEY = "'oa2hisdocInfo'";

    @Autowired
    DoctorService doctorService;

    @Autowired
    WebServicesClient webServicesClient;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;


    @CachePut(key = SUBJECT_MAP_KEY)
    public Map<String, String> cacheSubjectMap() throws Exception {
        List<Dept> deptInfoList = webService4HisInterfaceCacheUtil.getCacheDeptInfo();
        Map<String, String> subMap = new HashMap<>();
        for (Dept dept : deptInfoList) {
            subMap.put(dept.getDeptId(), dept.getDeptName());
        }
        return subMap;
    }

    @Cacheable(key = SUBJECT_MAP_KEY)
    public Map<String, String> getCacheSubjectMap() throws Exception {
        return cacheSubjectMap();
    }

    @CachePut(key = SUBJECTDOCUMENT_KEY)
    public Map<String, Map<String, List<SubjectDocument>>> cacheSubjectDocument() throws Exception {
        Map<String, Map<String, List<SubjectDocument>>> allSubMap = new HashMap<>();
        Map<String, List<SubjectDocument>> subjectMap = null;
        String levelCode = "";
        List<SubjectDocument> list = null;
        SubjectDocument sub = null;
        List<Dept> deptInfoList = webService4HisInterfaceCacheUtil.getCacheDeptInfo();
        Collections.sort(deptInfoList, new Comparator<Dept>() {
            @Override
            public int compare(Dept o1, Dept o2) {
                return o1.getSortId()-o2.getSortId();
            }
        });
        for (Dept dept : deptInfoList) {
            if (dept.getLevel() == DeptLevel.DOCTOR) {
                levelCode = String.valueOf(DeptLevel.DOCTOR.getCode());
            } else {
                levelCode = String.valueOf(DeptLevel.CHILD.getCode());
            }
            subjectMap = allSubMap.get(levelCode);
            if (subjectMap == null) {
                subjectMap = new HashMap<>();
                allSubMap.put(levelCode, subjectMap);
            }
            list = subjectMap.get(dept.getParentId());
            if (list == null) {
                list = new ArrayList<>();
                subjectMap.put(dept.getParentId(), list);
            }
            sub = new SubjectDocument();
            sub.setId(dept.getDeptId());
            sub.setName(dept.getDeptName());
            list.add(sub);
        }
        return allSubMap;
    }

    /**
     * 获得学科或医生
     *
     * @return
     */
    @Cacheable(key = SUBJECTDOCUMENT_KEY)
    public Map<String, Map<String, List<SubjectDocument>>> getCacheSubjectAndDoctorDocument() throws Exception {
        return this.cacheSubjectDocument();
    }

    @CachePut(key = OA2HISDOCINFO_KEY)
    public Map<String, Map<String, Object>> cacheOA2HISInfo() throws Exception {
        Map<String, List<SubjectDocument>> docMap = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.DOCTOR.getCode()));
        Set<String> docIdSet = new HashSet<>();
        for (List<SubjectDocument> list : docMap.values()) {
            for (SubjectDocument subjectDocument : list) {
                docIdSet.add(subjectDocument.getId());
            }

        }

        return doctorService.getOA2HISDoctorInfo(docIdSet);
    }

    @Cacheable(key = OA2HISDOCINFO_KEY)
    public Map<String, Map<String, Object>> getCacheOA2HISInfo() throws Exception {
        return this.cacheOA2HISInfo();
    }

}
