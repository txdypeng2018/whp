package com.proper.enterprise.isj.context;

import java.util.Date;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface StartDateObjContext<T> extends BusinessContext<T> {
    Date getStartDateObj();

    void setStartDateObj(Date startDate);
}
