package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface SearchTimeContext<T> extends BusinessContext<T> {
    String getSearchTime();

    void setSearchTime(String searchTime);
}
