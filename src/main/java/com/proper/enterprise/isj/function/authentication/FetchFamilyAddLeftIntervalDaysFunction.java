package com.proper.enterprise.isj.function.authentication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.ExpressionException;

import com.proper.enterprise.isj.business.RepositoryFunctionToolkit;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.SpELParser;

public class FetchFamilyAddLeftIntervalDaysFunction implements IFunction<Integer>, ILoggable {

    @Autowired
    SpELParser parser;

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    RepositoryFunctionToolkit toolkitx;

    @Override
    public Integer execute(Object... params) throws Exception {
        return this.getFamilyAddLeftIntervalDays((int) params[0], (String) params[1]);
    }

    public int getFamilyAddLeftIntervalDays(int familyMemberSize, String lastCreateTime) {
        int leftIntervalDays = 0;
        Collection<RuleEntity> rules = toolkitx.executeRepositoryFunction(RuleRepository.class, "findByCatalogue",
                "FAMILY_ADD_LIMIT");
        Map<String, Object> vars = new HashMap<>();
        vars.put("familyMemberSize", familyMemberSize);
        vars.put("lastCreateTime", lastCreateTime);
        if (rules != null && rules.size() > 0) {
            RuleEntity ruleEntity = rules.iterator().next();
            try {
                leftIntervalDays = parser.parse(ruleEntity.getRule(), vars, Integer.class);
            } catch (ExpressionException e) {
                debug("Parse {} with {} throw exception:", ruleEntity.getRule(), vars, e);
            }
        }
        return leftIntervalDays;
    }
}
