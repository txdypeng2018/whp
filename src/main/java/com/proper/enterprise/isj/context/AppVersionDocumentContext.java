package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface AppVersionDocumentContext<T> extends BusinessContext<T> {
    AppVersionDocument getAppVersionDocument();

    void setAppVersionDocument(AppVersionDocument appVersion);
}
