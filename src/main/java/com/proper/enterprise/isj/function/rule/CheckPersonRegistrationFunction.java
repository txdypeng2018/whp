package com.proper.enterprise.isj.function.rule;

import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.SpELParser;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CheckPersonRegistrationFunction implements IFunction<String>, ILoggable {

    @Autowired
    RuleRepository repository;

    @Autowired
    SpELParser parser;

    @Override
    public String execute(Object... params) throws Exception {
        return checkPersonRegistration((String) params[0], (String) params[1]);
    }

    public String checkPersonRegistration(String deptId, String idCard) {
        Map<String, Object> vars = new HashMap<>(2);
        vars.put("deptId", deptId);
        vars.put("idCard", idCard);
        String msg = "";

        try {
            Collection<RuleEntity> rules = repository.findByCatalogue("REG_RES");
            for (RuleEntity rule : rules) {
                try {
                    msg = parser.parse(rule.getRule(), vars, false);
                    if (StringUtil.isNotNull(msg)) {
                        break;
                    }
                } catch (ExpressionException ee) {
                    debug("Parse {} with {} throw exception:", rule.getRule(), vars, ee);
                }
            }
        } catch (RuntimeException re) {
            LOGGER.error("Exception occurs when finding rules by catalogue 'REG_RES'", re);
        }
        return msg;
    }

}
