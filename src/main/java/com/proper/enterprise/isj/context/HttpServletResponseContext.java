package com.proper.enterprise.isj.context;

import javax.servlet.http.HttpServletResponse;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface HttpServletResponseContext<T> extends BusinessContext<T> {
    HttpServletResponse getResponse();

    void setResponse(HttpServletResponse response);
}
