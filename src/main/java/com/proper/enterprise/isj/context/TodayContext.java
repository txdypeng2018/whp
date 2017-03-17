package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TodayContext<T> extends BusinessContext<T> {
    String getToday();

    void setToday(String today);
}
