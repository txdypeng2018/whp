package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.entity.BaseInfoEntity;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface BaseInfoEntityContext<T> extends BusinessContext<T> {
    BaseInfoEntity getBasicInfoEntity();
    void setBasicInfoEntity(BaseInfoEntity basicInfo);
}
