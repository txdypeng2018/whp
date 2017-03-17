package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface InfoContext<T> extends BusinessContext<T> {
    String getInfo();

    void setInfo(String info);
}
