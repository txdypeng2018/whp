package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RuleContext<T> extends BusinessContext<T> {
    String getRule();

    void setRule(String rule);
}
