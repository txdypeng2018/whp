package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface UserTelContext<T> extends BusinessContext<T> {
    String getUserTel();

    void setUserTel(String userTel);
}
