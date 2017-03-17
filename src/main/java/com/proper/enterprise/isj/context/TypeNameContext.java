package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TypeNameContext<T> extends BusinessContext<T> {
    String getTypeName();

    void setTypeName(String typeName);
}
