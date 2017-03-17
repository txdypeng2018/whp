package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.document.StopRegRecordDocument;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface StopRegRecordDocumentContext<T> extends BusinessContext<T> {
    StopRegRecordDocument getStopRegRecordDocument();

    void setStopRegRecordDocument(StopRegRecordDocument statusCode);
}
