package com.proper.enterprise.isj.context;

import java.util.Date;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface EndDateObjContext<T> extends BusinessContext<T> {
    Date getEndDateObj();

    void setEndDateObj(Date endDate);
}
