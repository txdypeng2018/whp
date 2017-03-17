package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface OpinionContext<T> extends BusinessContext<T> {
    String getOpinion();

    void setOpinion(String opinion);
}
