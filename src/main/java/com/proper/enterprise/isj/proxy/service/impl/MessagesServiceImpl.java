package com.proper.enterprise.isj.proxy.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MessageDocumentCollectionContext;
import com.proper.enterprise.isj.context.MessagesDocumentContext;
import com.proper.enterprise.isj.context.UserIdContext;
import com.proper.enterprise.isj.proxy.business.message.FetchMessagesDocumentsBusiness;
import com.proper.enterprise.isj.proxy.business.message.SaveMessageBusiness;
import com.proper.enterprise.isj.proxy.business.message.SaveMessagesBusiness;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.support.service.AbstractService;

/**
 * 消息ServiceImpl.
 */
@Service
public class MessagesServiceImpl extends AbstractService implements MessagesService {

    /**
     * 保存消息并进行推送(对一个人发送推送消息).
     *
     * @param messageDocument 消息报文.
     * @throws Exception 异常.
     */
    @Override
    public void saveMessage(MessagesDocument messageDocument) throws Exception {
        toolkit.execute(SaveMessageBusiness.class,
                ctx -> ((MessagesDocumentContext<?>) ctx).setMessagesDocument(messageDocument));
    }

    /**
     * 保存消息并进行推送(对多人推送同一消息).
     *
     * @param messageList 消息列表.
     * @throws Exception 异常.
     */
    @Override
    public void saveMessages(List<MessagesDocument> messageList) throws Exception {
        toolkit.execute(SaveMessagesBusiness.class,
                ctx -> ((MessageDocumentCollectionContext<?>) ctx).setMessagesDocuments(messageList));
    }

    /**
     * 根据用户获取消息列表.
     *
     * @param userId 用户ID.
     * @return 消息列表.
     */
    @Override
    public List<MessagesDocument> findMessagesDocumentList(String userId) {
        return toolkit.execute(FetchMessagesDocumentsBusiness.class, ctx -> ((UserIdContext<?>) ctx).setUserId(userId));
    }

}
