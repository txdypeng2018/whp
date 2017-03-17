package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface StatusCodeContext<T> extends BusinessContext<T> {
    String getStatusCode();

    void setStatusCode(String statusCode);
}
