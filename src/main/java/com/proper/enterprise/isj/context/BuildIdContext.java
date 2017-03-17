package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface BuildIdContext<T> extends BusinessContext<T> {
    String getBuildId();

    void setBuildId(String buildId);
}
