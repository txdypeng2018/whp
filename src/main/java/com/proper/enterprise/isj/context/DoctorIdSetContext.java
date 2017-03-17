package com.proper.enterprise.isj.context;

import java.util.Set;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface DoctorIdSetContext<T> extends BusinessContext<T> {
    Set<String> getDoctorIds();

    void setDoctorIds(Set<String> doctorIds);
}
