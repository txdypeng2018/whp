package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface BuildingNameContext<T> extends BusinessContext<T> {
    String getBuildingName();

    void setBuildingName(String buildingName);
}
