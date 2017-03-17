package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TipInfoTypeContext<T> extends BusinessContext<T> {
    String getTipInfoType();

    void setTipInfoType(String infoType);
}
