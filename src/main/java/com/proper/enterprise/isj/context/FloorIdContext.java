package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface FloorIdContext<T> extends BusinessContext<T> {
    String getFloorId();

    void setFloorId(String floorId);
}
