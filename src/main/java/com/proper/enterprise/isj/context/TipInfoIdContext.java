package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TipInfoIdContext<T> extends BusinessContext<T> {
    String getTipInfoId();

    void setTipInfoId(String id);
}
