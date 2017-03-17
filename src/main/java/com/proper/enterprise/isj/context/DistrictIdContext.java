package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface DistrictIdContext<T> extends BusinessContext<T> {
    String getDistrictId();

    void setDistrictId(String districtId);
}
