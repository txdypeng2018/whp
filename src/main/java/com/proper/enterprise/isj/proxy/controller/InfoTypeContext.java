package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface InfoTypeContext<T> extends BusinessContext<T> {
    String getInfoType();

    void setInfoType(String infoType);
}
