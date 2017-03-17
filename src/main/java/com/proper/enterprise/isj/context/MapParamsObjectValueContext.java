package com.proper.enterprise.isj.context;

import java.util.Map;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface MapParamsObjectValueContext<T> extends BusinessContext<T> {
    Map<String, Object> getMapParams();

    void setMapParams(Map<String, Object> mapObjectVal);
}
