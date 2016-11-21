package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.entity.RegistrationRulesEntity;
import com.proper.enterprise.isj.rule.entity.RuleEntity;

import java.util.List;

/**
 * 挂号规则Service
 */
public interface RegistrationRulesService {

    RegistrationRulesEntity getRulesInfo(String catalogue, String ruleName, String rule, String pageNo, String pageSize) throws Exception;

    void saveRuleInfo(RuleEntity ruleInfo) throws Exception;

    void deleteRuleInfo(List<String> idList) throws Exception;

    RuleEntity getRuleInfoById(String id) throws Exception;
}
