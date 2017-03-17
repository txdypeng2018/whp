package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface FloorParentIdContext<T> extends BusinessContext<T> {
    String getFloorParentId();

    void setFloorParentId(String floorParentId);
}
