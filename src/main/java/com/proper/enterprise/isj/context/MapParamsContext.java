package com.proper.enterprise.isj.context;

import java.util.Map;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface MapParamsContext<T> extends BusinessContext<T> {
    Map<String, String> getParams();

    void setParams(Map<String, String> params);
}
