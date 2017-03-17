package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RuleNameContext<T> extends BusinessContext<T> {
    String getRuleName();

    void setRuleName(String name);
}