package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface SearchStatusContext<T> extends BusinessContext<T> {
    String getSearchStatus();

    void setSearchStatus(String searchStatus);
}
