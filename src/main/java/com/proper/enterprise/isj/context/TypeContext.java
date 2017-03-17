package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TypeContext<T> extends BusinessContext<T> {
    String getType();

    void setType(String type);
}
