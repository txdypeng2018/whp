package com.proper.enterprise.isj.function.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.sort.CNStrComparator;

/**
 * com.proper.enterprise.isj.proxy.service.impl.ScheduleServiceImpl.getScheDoctorList(String, String)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 * @param <L>
 */
@Service
public class HisFetchScheDoctorListFunction<L extends Collection<DoctorDocument>> implements IFunction<L> {
    

    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;
    
    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;
    
    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public L execute(Object... params) throws Exception {
        return (L)getScheDoctorList((String) params[0], (String) params[1]);
    }


    /**
     * 获得过滤好的医生集合.
     *
     * @param deptId 科室.
     * @param major  专业.
     * @return 医生列表.
     * @throws Exception 异常.
     */
    private List<DoctorDocument> getScheDoctorList(String deptId, String major) throws Exception {
        Map<String, List<SubjectDocument>> subMap = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument().get(String.valueOf(DeptLevel.DOCTOR.getCode()));
        List<SubjectDocument> disList = new ArrayList<>();
        Set<String> idSet = null;
        if (StringUtil.isNotEmpty(deptId)) {
            disList = subMap.get(deptId);
            if (disList == null) {
                disList = new ArrayList<>();
            }
        } else {
            for (Map.Entry<String, List<SubjectDocument>> entry : subMap.entrySet()) {
                disList.addAll(subMap.get(entry.getKey()));
            }
        }
        if (StringUtil.isNotEmpty(major)) {
            idSet = new HashSet<>();
            Map<String, Set<String>> likeMap = webServiceCacheUtil.getCacheDoctorInfoLike();
            for (Map.Entry<String, Set<String>> entry : likeMap.entrySet()) {
                if (entry.getKey().contains(major)) {
                    idSet.addAll(likeMap.get(entry.getKey()));
                }
            }
        }
        Set<String> doctorIdSet = new HashSet<>();
        DoctorDocument doc;
        List<DoctorDocument> scheDoctorList = new ArrayList<>();
        for (SubjectDocument districtDocument : disList) {
            if (idSet != null && !idSet.contains(districtDocument.getId())) {
                continue;
            }
            if (doctorIdSet.contains(districtDocument.getId())) {
                continue;
            }
            doctorIdSet.add(districtDocument.getId());
            doc = webServiceCacheUtil.getCacheDoctorDocument().get(districtDocument.getId());
            if (doc != null) {
                scheDoctorList.add(doc);
            }
        }
        Collections.sort(scheDoctorList, new Comparator<DoctorDocument>() {
            @Override
            public int compare(DoctorDocument doc1, DoctorDocument doc2) {
                int seq1 = CenterFunctionUtils.getDoctorTitleSeq(doc1.getTitle());
                int seq2 = CenterFunctionUtils.getDoctorTitleSeq(doc2.getTitle());
                if (seq1 - seq2 == 0) {
                    return new CNStrComparator().compare(doc1.getName(), doc2.getName());
                } else {
                    return seq1 - seq2;
                }
            }
        });
        return scheDoctorList;
    }

}