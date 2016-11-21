package com.proper.enterprise.isj.proxy.service.rule.impl;

import com.proper.enterprise.isj.proxy.service.rule.RegistrationRuleService;
import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.isj.rule.repository.RuleRepository;
import com.proper.enterprise.platform.core.utils.SpELParser;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by think on 2016/9/27 0027.
 */
@Service
public class RegistrationRuleServiceImpl implements RegistrationRuleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationRuleServiceImpl.class);

    @Autowired
    RuleRepository repository;

    @Autowired
    SpELParser parser;

    @Override
    public String checkPersonRegistration(String deptId, String idCard) {
        Map<String, Object> vars = new HashMap<>(2);
        vars.put("deptId", deptId);
        vars.put("idCard", idCard);
        String msg = "";
        Collection<RuleEntity> rules = repository.findByCatalogue("REG_RES");
        for (RuleEntity rule : rules) {
            try {
                msg = parser.parse(rule.getRule(), vars, false);
                if (StringUtil.isNotNull(msg)) {
                    break;
                }
            } catch (ExpressionException ee) {
                LOGGER.debug("Parse {} with {} throw exception:", rule.getRule(), vars, ee);
            }
        }
        return msg;
    }
}
