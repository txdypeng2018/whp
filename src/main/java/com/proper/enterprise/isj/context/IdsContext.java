package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface IdsContext<T> extends BusinessContext<T> {
    String getIds();

    void setIds(String ids);
}
