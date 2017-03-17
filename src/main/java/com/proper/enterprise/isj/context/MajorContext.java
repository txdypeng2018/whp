package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface MajorContext<T> extends BusinessContext<T> {
    String getMajor();

    void setMajor(String major);
}
