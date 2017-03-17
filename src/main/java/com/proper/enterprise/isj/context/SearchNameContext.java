package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface SearchNameContext<T> extends BusinessContext<T> {
    String getSearchName();

    void setSearchName(String searchName);
}
