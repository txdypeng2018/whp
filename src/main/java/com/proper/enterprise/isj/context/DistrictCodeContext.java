package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface DistrictCodeContext<T> extends BusinessContext<T> {
    String getDistrictCode();

    void setDistrictCode(String districtCode);
}
