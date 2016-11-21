package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.SubjectService;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.core.utils.SpELParser;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubjectServiceImpl implements SubjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectServiceImpl.class);

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    RuleRepository ruleRepository;

    @Autowired
    SpELParser parser;

    @Override
    @Cacheable(cacheNames = "pep-temp_600")
    public List<SubjectDocument> findSubjectDocumentListFromHis(String districtId, boolean isAppointment) throws Exception {
        return mergeDept2Subject(districtId, isAppointment);
    }

    private List<SubjectDocument> mergeDept2Subject(String rootParentId, boolean isAppointment) throws Exception {
        List<SubjectDocument> result = new ArrayList<>();
        if (StringUtil.isNull(rootParentId)) {
            return result;
        }

        List<SubjectDocument> subjectList = webServiceDataSecondCacheUtil.getCacheSubjectAndDoctorDocument()
                .get(String.valueOf(DeptLevel.CHILD.getCode())).get(rootParentId);
        if (subjectList == null || subjectList.isEmpty()) {
            return result;
        }

        Collection<RuleEntity> rules = ruleRepository.findByCatalogue("SUBJECT_FILTER");
        Map<String, Object> vars = new HashMap<>(2);
        vars.put("isAppointment", isAppointment);
        boolean filter;
        for (SubjectDocument doc : subjectList) {
            filter = false;
            for (RuleEntity rule : rules) {
                vars.put("subjectId", doc.getId());
                try {
                    if (parser.parse(rule.getRule(), vars, Boolean.class)) {
                        filter = true;
                        break;
                    }
                } catch (ExpressionException ee) {
                    LOGGER.debug("Parse {} with {} throw exception:", rule.getRule(), vars, ee);
                }
            }
            if (!filter) {
                doc.setSubjects(this.mergeDept2Subject(doc.getId(), isAppointment));
                result.add(doc);
            }
        }
        return result;
    }

}
