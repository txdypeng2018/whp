package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;

public interface RegistrationDocIdContext<T> extends BusinessContext<T> {
    String getRegistrationDocumentId();

    void setRegistrationDocumentId(String regDocId);
}
