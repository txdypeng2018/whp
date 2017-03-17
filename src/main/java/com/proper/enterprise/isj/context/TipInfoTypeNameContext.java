package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TipInfoTypeNameContext<T> extends BusinessContext<T> {
    String getTipInfoTypeName();

    void setTipInfoTypeName(String infoTypeName);
}
