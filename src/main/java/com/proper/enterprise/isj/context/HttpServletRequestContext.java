package com.proper.enterprise.isj.context;

import javax.servlet.http.HttpServletRequest;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface HttpServletRequestContext<T> extends BusinessContext<T> {
    HttpServletRequest getRequest();

    void setRequest(HttpServletRequest request);
}
