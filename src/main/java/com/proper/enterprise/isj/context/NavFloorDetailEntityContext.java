package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.entity.NavigationFloorDetailEntity;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface NavFloorDetailEntityContext<T> extends BusinessContext<T> {

    NavigationFloorDetailEntity getNavFloorDetail();

    void setNavFloorDetail(NavigationFloorDetailEntity entity);
}
