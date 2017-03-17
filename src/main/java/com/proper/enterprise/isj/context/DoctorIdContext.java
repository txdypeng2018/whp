package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface DoctorIdContext<T> extends BusinessContext<T> {
    String getDoctorId();

    void setDoctorId(String doctorId);
}
