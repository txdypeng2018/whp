package com.proper.enterprise.isj.context;

import java.util.Collection;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TipInfoIdsContext<T> extends BusinessContext<T> {
    Collection<String> getTipInfoIds();

    void setTipInfoIds(Collection<String> tipInfo);
}
