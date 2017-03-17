package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface PayStatusContext<T> extends BusinessContext<T> {

    String getPayStatus();

    void setPayStatus(String status);
}
