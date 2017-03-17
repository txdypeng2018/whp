package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface EndDateContext<T> extends BusinessContext<T> {
    String getEndDate();

    void setEndDate(String endDate);
}
