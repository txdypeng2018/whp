package com.proper.enterprise.isj.context;

import java.util.Collection;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface MessageDocumentCollectionContext<T> extends BusinessContext<T> {
    Collection<MessagesDocument> getMessagesDocuments();

    void setMessagesDocuments(Collection<MessagesDocument> messagesDocuments);
}
