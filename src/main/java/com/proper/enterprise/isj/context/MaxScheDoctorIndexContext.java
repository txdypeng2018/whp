package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface MaxScheDoctorIndexContext<T> extends BusinessContext<T> {
    int getMaxScheDoctorIndex();

    void setMaxScheDoctorIndex(int maxScheDoctorIndex);
}
