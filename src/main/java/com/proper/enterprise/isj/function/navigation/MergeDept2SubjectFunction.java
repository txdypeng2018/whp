package com.proper.enterprise.isj.function.navigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.SpELParser;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * old:com.proper.enterprise.isj.proxy.service.impl.SubjectServiceImpl.mergeDept2Subject(String, boolean)
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 */
@Service
public class MergeDept2SubjectFunction implements IFunction<List<SubjectDocument>>, ILoggable {

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    RuleRepository ruleRepository;

    @Autowired
    SpELParser parser;
    
    @Override
    public List<SubjectDocument> execute(Object... params) throws Exception {
        return mergeDept2Subject((String)params[0], (Boolean)params[1]);
    }
    
    public List<SubjectDocument> mergeDept2Subject(String rootParentId, boolean isAppointment) throws Exception {
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
                    debug("Parse {} with {} throw exception:", rule.getRule(), vars, ee);
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
