package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface HourMinContext<T> extends BusinessContext<T> {
    String getHourMin();

    void setHourMin(String hourMin);
}
