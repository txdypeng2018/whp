package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface IsAppointmentContext<T> extends BusinessContext<T> {
    String getIsAppointment();

    void setIsAppointment(String isAppointment);
}
