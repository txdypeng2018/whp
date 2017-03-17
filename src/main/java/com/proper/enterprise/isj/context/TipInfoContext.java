package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TipInfoContext<T> extends BusinessContext<T> {

    String getTipInfo();

    void setTipInfo(String tipInfo);
}