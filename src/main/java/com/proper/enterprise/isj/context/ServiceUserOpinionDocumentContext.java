package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.document.ServiceUserOpinionDocument;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface ServiceUserOpinionDocumentContext<T> extends BusinessContext<T> {
    ServiceUserOpinionDocument getOpinionDocment();

    void setOpinionDocment(ServiceUserOpinionDocument opinionDocment);
}
