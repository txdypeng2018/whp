package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface UserNameContext<T> extends BusinessContext<T> {
    String getUserName();

    void setUserName(String userName);
}
