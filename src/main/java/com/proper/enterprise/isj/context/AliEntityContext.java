package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.pay.ali.entity.AliEntity;

public interface AliEntityContext<T> extends BusinessContext<T> {
    AliEntity getAliInfo();

    void setAliInfo(AliEntity aliInfo);
}