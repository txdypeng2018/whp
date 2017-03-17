package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RuleInfoIdContext<T> extends BusinessContext<T> {
    String getRuleInfoId();

    void setRuleInfoId(String ids);
}
