package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface DateContext<T> extends BusinessContext<T> {
    String getDate();

    void setDate(String date);
}
