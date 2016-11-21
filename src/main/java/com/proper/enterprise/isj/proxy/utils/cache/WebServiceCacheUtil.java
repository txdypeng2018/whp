package com.proper.enterprise.isj.proxy.utils.cache;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.DoctorService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.isj.webservices.model.res.HosInfo;
import com.proper.enterprise.isj.webservices.model.res.doctorinfo.Doctor;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.sort.CNStrComparator;

/**
 * Created by think on 2016/9/20 0020.
 */
@Component
@CacheConfig(cacheNames = "pep-temp")
public class WebServiceCacheUtil {

    private static final String DOCTOR_SUBJECT_REL_KEY = "'doctor_subject_rel'";

    private static final String DOCTORINFO_LIKE_KEY = "'doctorinfo_like'";

    private static final String DOCTORINFO_KEY = "'doctorDocument'";

    private static final String VERIFICATIONCODE_KEY = "'verificationcodeMap'";

    @Autowired
    DoctorService doctorService;

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;



    /**
     * 医生科室对应关系
     *
     * @return
     */
    @CachePut(key = DOCTOR_SUBJECT_REL_KEY)
    public Map<String, String> cacheDoctorSubjectRelMap() throws Exception {
        Map<String, List<SubjectDocument>> docMap = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.DOCTOR.getCode()));
        Iterator<Map.Entry<String, List<SubjectDocument>>> docIter = docMap.entrySet().iterator();
        String subjectId = null;
        List<SubjectDocument> docList = null;
        Map<String, String> docSubjectRelMap = new HashMap<>();
        while (docIter.hasNext()) {
            Map.Entry<String, List<SubjectDocument>> entry = docIter.next();
            subjectId = entry.getKey();
            docList = entry.getValue();
            for (SubjectDocument subjectDocument : docList) {
                docSubjectRelMap.put(subjectDocument.getId(), subjectId);
            }
        }
        return docSubjectRelMap;
    }

    /**
     * 获得医生科室对应关系
     *
     * @return
     * @throws Exception
     */
    @Cacheable(key = DOCTOR_SUBJECT_REL_KEY)
    public Map<String, String> getCacheDoctorSubjectRelMap() throws Exception {
        return this.cacheDoctorSubjectRelMap();
    }

    @CachePut(key = DOCTORINFO_KEY)
    public Map<String, DoctorDocument> cacheDoctorDocument() throws Exception {
        List<Doctor> doctorList = webService4HisInterfaceCacheUtil.getCacheDoctorInfoRes();
        Map<String, List<SubjectDocument>> docMap = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.DOCTOR.getCode()));
        Set<String> docIdSet = new HashSet<>();
        for (List<SubjectDocument> list : docMap.values()) {
            for (SubjectDocument subjectDocument : list) {
                docIdSet.add(subjectDocument.getId());
            }

        }
        Map<String, Map<String, Object>> oaInfoMap = webServiceDataSecondCacheUtil.getCacheOA2HISInfo();
        Map<String, DoctorDocument> doctorMap = new HashMap<>();
        DoctorDocument tempDoc = null;
        for (Doctor doctor : doctorList) {
            if (docIdSet.contains(doctor.getDoctorId())) {
                tempDoc = this.convertDoctor2Document(doctor, oaInfoMap.get(doctor.getDoctorId()));
                // if(StringUtil.isEmpty(tempDoc.getDept())){
                // continue;
                // }
                doctorMap.put(doctor.getDoctorId(), tempDoc);
            }
        }
        List<DoctorDocument> resList = new ArrayList<>();
        resList.addAll(doctorMap.values());

        Collections.sort(resList, new Comparator<DoctorDocument>() {
            @Override
            public int compare(DoctorDocument doc1, DoctorDocument doc2) {
                int seq1 = CenterFunctionUtils.getDoctorTitleSeq(doc1.getTitle());
                int seq2 = CenterFunctionUtils.getDoctorTitleSeq(doc2.getTitle());
                if(seq1-seq2==0){
                    return new CNStrComparator().compare(doc1.getName(), doc2.getName());
                }else{
                    return seq1-seq2;
                }

            }
        });
        Map<String, DoctorDocument> resultMap = new LinkedHashMap<>();
        for (DoctorDocument doctorDocument : resList) {
            resultMap.put(doctorDocument.getId(), doctorDocument);
        }
        return resultMap;
    }

    @Cacheable(key = DOCTORINFO_KEY)
    public Map<String, DoctorDocument> getCacheDoctorDocument() throws Exception {
        return this.cacheDoctorDocument();
    }

    private DoctorDocument convertDoctor2Document(Doctor doctor, Map<String, Object> oaInfoMap) throws Exception {
        HosInfo hosInfo = webService4HisInterfaceCacheUtil.getCacheHospitalInfoRes();
        DoctorDocument doc;
        doc = new DoctorDocument();
        doc.setId(doctor.getDoctorId());
        doc.setName(doctor.getName());
        doc.setIdCard(doctor.getIdCard());
        if (oaInfoMap != null) {
            doc.setDistrict("");
            doc.setDept((String) oaInfoMap.get("KS"));
            doc.setSummary((String) oaInfoMap.get("JJ"));
            doc.setSkill((String) oaInfoMap.get("TC"));
            doc.setPersonPic((String) oaInfoMap.get("ZP"));
        }

        if(doctor.getJobTitle()!=null){
            doc.setTitle(doctor.getJobTitle());
        }
        if (doctor.getRegFee() != null) {
            doc.setRegFee(doctor.getRegFee().toString());
        }
        if (doctor.getStatus() != null) {
            doc.setStatus(doctor.getStatus().toString());
        }
        if (doctor.getSex() != null) {
            doc.setSexCode(doctor.getSex().toString());
        }
        if (StringUtil.isNotEmpty(doctor.getBirthday())) {
            doc.setBirthday(doctor.getBirthday());
        }
        doc.setPhone(doctor.getMobile());
        doc.setWorkPhone(doctor.getTel());
        doc.setHospital(hosInfo.getName());
        return doc;
    }

    @CachePut(key = DOCTORINFO_LIKE_KEY)
    public Map<String, Set<String>> cacheDoctorInfoLike() throws Exception {
        Map<String, DoctorDocument> docMap = this.getCacheDoctorDocument();
        Map<String, Set<String>> infoMap = new HashMap<>();
        Set<String> docIdSet = null;
        for (DoctorDocument doctorDocument : docMap.values()) {
            if (StringUtil.isNotEmpty(doctorDocument.getName())) {
                docIdSet = infoMap.get(doctorDocument.getName());
                if (docIdSet == null) {
                    docIdSet = new HashSet<>();
                    infoMap.put(doctorDocument.getName(), docIdSet);
                }
                docIdSet.add(doctorDocument.getId());
            }

            if (StringUtil.isNotEmpty(doctorDocument.getSkill())) {
                docIdSet = infoMap.get(doctorDocument.getSkill());
                if (docIdSet == null) {
                    docIdSet = new HashSet<>();
                    infoMap.put(doctorDocument.getSkill(), docIdSet);
                }
                docIdSet.add(doctorDocument.getId());
            }
            if (StringUtil.isNotEmpty(doctorDocument.getSummary())) {
                docIdSet = infoMap.get(doctorDocument.getSummary());
                if (docIdSet == null) {
                    docIdSet = new HashSet<>();
                    infoMap.put(doctorDocument.getSummary(), docIdSet);
                }
                docIdSet.add(doctorDocument.getId());
            }
        }
        return infoMap;
    }

    @Cacheable(key = DOCTORINFO_LIKE_KEY)
    public Map<String, Set<String>> getCacheDoctorInfoLike() throws Exception {
        return cacheDoctorInfoLike();
    }
}
