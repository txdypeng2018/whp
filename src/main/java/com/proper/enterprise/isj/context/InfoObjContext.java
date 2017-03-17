package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface InfoObjContext<T> extends BusinessContext<T> {

    Object getInfoObj();

    void setInfoObj(Object infoObj);

}
