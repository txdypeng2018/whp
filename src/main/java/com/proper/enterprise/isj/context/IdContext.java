package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface IdContext<T> extends BusinessContext<T> {
    String getId();

    void setId(String id);
}
