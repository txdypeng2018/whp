package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface PageNoContext<T> extends BusinessContext<T> {
    Integer getPageNo();

    void setPageNo(Integer pageNo);
}
