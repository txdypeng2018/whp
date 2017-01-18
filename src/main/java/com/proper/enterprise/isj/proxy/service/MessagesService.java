package com.proper.enterprise.isj.proxy.service;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;

import java.util.List;

/**
 * 消息Service.
 */
public interface MessagesService {

    void saveMessage(MessagesDocument messageDocument) throws Exception;

    void saveMessages(List<MessagesDocument> messageList) throws Exception;

    List<MessagesDocument> findMessagesDocumentList(String userId);

}
