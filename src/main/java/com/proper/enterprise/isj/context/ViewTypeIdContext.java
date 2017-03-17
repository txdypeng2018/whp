package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface ViewTypeIdContext<T> extends BusinessContext<T> {
    String getViewTypeId();

    void setViewTypeId(String viewTypeId);
}
