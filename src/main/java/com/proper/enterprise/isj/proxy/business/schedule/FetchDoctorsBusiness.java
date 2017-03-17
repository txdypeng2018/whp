package com.proper.enterprise.isj.proxy.business.schedule;

import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.APP_SYSTEM_ERR;
import static com.proper.enterprise.isj.user.utils.CenterFunctionUtils.HIS_DATALINK_ERR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.PageNoContext;
import com.proper.enterprise.isj.context.SearchNameContext;
import com.proper.enterprise.isj.context.SubjectIdContext;
import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.DoctorService;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4FileCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchDoctorsBusiness<T, M extends PageNoContext<Object> & SubjectIdContext<Object> & SearchNameContext<Object> & ModifiedResultBusinessContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    @Autowired
    DoctorService doctorService;

    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    WebService4FileCacheUtil webService4FileCacheUtil;

    @Override
    public void process(M ctx) throws Exception {
        Integer pageNo = ctx.getPageNo();
        String subjectId = ctx.getSubjectId();
        String searchName = ctx.getSearchName();

        List<DoctorDocument> docList;
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> resultDocMap;
        try {
            if (pageNo == null) {
                pageNo = 1;
            }
            int numForPage = CenterFunctionUtils.DEFAULT_PAGE_NUM;
            int fromIndex = (pageNo - 1) * numForPage;
            int toIndex = fromIndex + numForPage;
            docList = new ArrayList<>();
            Map<String, DoctorDocument> doctorMap = webServiceCacheUtil.getCacheDoctorDocument();
            Set<String> subSet = null;
            if (StringUtil.isNotEmpty(subjectId)) {
                Map<String, List<SubjectDocument>> allDocMap = webServiceDataSecondCacheUtil
                        .getCacheSubjectAndDoctorDocument().get(String.valueOf(DeptLevel.DOCTOR.getCode()));
                List<SubjectDocument> subList = allDocMap.get(subjectId);
                subSet = new HashSet<>();
                for (SubjectDocument subjectDocument : subList) {
                    subSet.add(subjectDocument.getId());
                }
            }
            if (StringUtil.isNotEmpty(searchName)) {
                Map<String, Set<String>> mapSet = webServiceCacheUtil.getCacheDoctorInfoLike();
                Iterator<Map.Entry<String, Set<String>>> mapIter = mapSet.entrySet().iterator();
                String key;
                Set<String> docIdSet;
                Set<String> resultSet = new HashSet<>();
                while (mapIter.hasNext()) {
                    Map.Entry<String, Set<String>> entry = mapIter.next();
                    key = entry.getKey();
                    docIdSet = entry.getValue();
                    if (key.contains(searchName)) {
                        resultSet.addAll(docIdSet);
                    }
                }
                Iterator<Map.Entry<String, DoctorDocument>> iter = doctorMap.entrySet().iterator();
                Map.Entry<String, DoctorDocument> entry;
                while (iter.hasNext()) {
                    entry = iter.next();
                    if (subSet != null && !subSet.contains(entry.getKey())) {
                        continue;
                    }
                    if (resultSet.contains(entry.getKey())) {
                        docList.add(entry.getValue());
                    }
                }
            } else {
                if (subSet != null) {
                    for (String docId : subSet) {
                        doctorMap.remove(docId);
                    }
                }
                docList.addAll(doctorMap.values());
            }
            if (toIndex > docList.size()) {
                toIndex = docList.size();
            }
            if (fromIndex <= docList.size()) {
                docList = docList.subList(fromIndex, toIndex);
                for (DoctorDocument doctorDocument : docList) {
                    resultDocMap = new HashMap<>();
                    resultDocMap.put("id", doctorDocument.getId());
                    resultDocMap.put("name", doctorDocument.getName());
                    resultDocMap.put("sexCode", doctorDocument.getSexCode());
                    resultDocMap.put("title", doctorDocument.getTitle());
                    resultDocMap.put("districtId", doctorDocument.getDistrictId());
                    resultDocMap.put("district", doctorDocument.getDistrict());
                    resultDocMap.put("deptId", doctorDocument.getDeptId());
                    resultDocMap.put("dept", doctorDocument.getDept());
                    if (StringUtil.isEmpty(doctorDocument.getSkill())) {
                        resultDocMap.put("skill", "");
                    } else if (doctorDocument.getSkill().length() < 30) {
                        resultDocMap.put("skill", doctorDocument.getSkill());
                    } else {
                        resultDocMap.put("skill", StringUtil.abbreviate(doctorDocument.getSkill(), 30));
                    }
                    result.add(resultDocMap);
                }
            } else {
                result = new ArrayList<>();
            }
        } catch (UnmarshallingFailureException e) {
            debug("解析HIS接口返回参数错误", e);
            throw new RuntimeException(HIS_DATALINK_ERR, e);
        } catch (HisReturnException e) {
            debug("HIS接口返回错误", e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            debug("系统错误", e);
            throw new RuntimeException(APP_SYSTEM_ERR, e);
        }
        ctx.setResult(result);
    }
}
