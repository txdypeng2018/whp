package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.rule.entity.RuleEntity;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RuleEntityContext<T> extends BusinessContext<T> {
    RuleEntity getRuleEntity();

    void setRuleEntity(RuleEntity rule);
}
