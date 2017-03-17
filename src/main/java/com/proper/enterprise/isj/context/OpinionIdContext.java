package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface OpinionIdContext<T> extends BusinessContext<T> {
    String getOpinionId();

    void setOpinionId(String opinionId);
}
