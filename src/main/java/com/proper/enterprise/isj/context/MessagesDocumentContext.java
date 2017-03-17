package com.proper.enterprise.isj.context;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.platform.core.api.BusinessContext;

public interface MessagesDocumentContext<T> extends BusinessContext<T> {
    MessagesDocument getMessagesDocument();

    void setMessagesDocument(MessagesDocument messagesDocument);
}
