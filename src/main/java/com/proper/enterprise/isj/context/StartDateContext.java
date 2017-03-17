package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface StartDateContext<T> extends BusinessContext<T> {
    String getStartDate();

    void setStartDate(String startDate);
}
