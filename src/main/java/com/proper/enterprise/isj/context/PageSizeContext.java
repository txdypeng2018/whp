package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface PageSizeContext<T> extends BusinessContext<T> {
    Integer getPageSize();

    void setPageSize(Integer pageSize);
}
