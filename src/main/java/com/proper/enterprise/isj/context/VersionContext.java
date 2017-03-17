package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;

public interface VersionContext<T> extends ModifiedResultBusinessContext<T> {
    String getVersion();

    void setVersion(String version);
}
