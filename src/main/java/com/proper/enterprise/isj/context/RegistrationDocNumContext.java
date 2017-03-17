package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RegistrationDocNumContext<T> extends BusinessContext<T> {
    String getRegistrationDocumentNum();

    void setRegistrationDocumentNum(String regDocNum);
}
