package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface IsAppointmentBooleanContext<T> extends BusinessContext<T> {
    Boolean getIsAppointmentBooleanValue();

    void setIsAppointmentBooleanValue(Boolean val);
}
