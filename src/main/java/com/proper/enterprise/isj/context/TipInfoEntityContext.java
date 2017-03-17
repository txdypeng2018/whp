package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface TipInfoEntityContext<T> extends BusinessContext<T> {
    BaseInfoEntity getTipInfoEntity();

    void setTipInfoEntity(BaseInfoEntity tipInfo);
}
