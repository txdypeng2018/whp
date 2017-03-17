package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.entity.NavigationBuildDetailEntity;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface BuildInfoEntityContext<T> extends BusinessContext<T> {
    NavigationBuildDetailEntity getBuildInfo();

    void setBuildInfo(NavigationBuildDetailEntity buildInfo);
}
