package com.proper.enterprise.isj.context;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RuleInfoIdsContext<T> extends BusinessContext<T> {
    Collection<String> getRuleInfoIds();

    void setRuleInfoIds(Collection<String> ids);
}
