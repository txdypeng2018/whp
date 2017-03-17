package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface PhoneContext<T> extends BusinessContext<T> {
    String getPhone();

    void setPhone(String phone);
}
