package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TypeCodeContext<T> extends BusinessContext<T> {
    String getTypeCode();

    void setTypeCode(String typeCode);
}
