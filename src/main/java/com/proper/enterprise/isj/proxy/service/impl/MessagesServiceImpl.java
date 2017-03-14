package com.proper.enterprise.isj.proxy.service.impl;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.repository.MessagesRepository;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.mobile.pushtools.PushMessage;
import com.proper.mobile.pushtools.PusherApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息ServiceImpl.
 */
@Service
public class MessagesServiceImpl implements MessagesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagesServiceImpl.class);

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserService userService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MessagesRepository messagesRepo;

    /**
     * 保存消息并进行推送(对一个人发送推送消息).
     *
     * @param messageDocument 消息报文.
     * @throws Exception 异常.
     */
    @Override
    public void saveMessage(MessagesDocument messageDocument) throws Exception {
        LOGGER.debug("Push info: {}", JSONUtil.toJSON(messageDocument));
        // 保存消息信息
        messagesRepo.save(messageDocument);

        // 获取推送相关参数
        String appkey= ConfCenter.get("isj.push.properpushAppkey");
        String secureKey = ConfCenter.get("isj.push.properpushSecert");
        String pushUrl = ConfCenter.get("isj.push.pushUrl");
        PusherApp app = new PusherApp(pushUrl, appkey, secureKey);
        app.setAsync(true);
        // 拼接推送消息
        PushMessage msg = new PushMessage();
        msg.setTitle("掌上盛京医院");
        msg.setContent(messageDocument.getContent());
        msg.addCustomData("pageUrl", "messages");
        LOGGER.debug("Invoke saveMessage to push {} to {}", messageDocument.getContent(), messageDocument.getUserName());
        // 推送消息
        app.pushMessageToOneUser(msg, messageDocument.getUserName());
    }

    /**
     * 保存消息并进行推送(对多人推送同一消息).
     *
     * @param messageList 消息列表.
     * @throws Exception 异常.
     */
    @Override
    public void saveMessages(List<MessagesDocument> messageList) throws Exception {
        LOGGER.debug("Push info: {}", JSONUtil.toJSON(messageList));
        // 取得推送消息
        String content = messageList.get(0).getContent();
        // 取得userNameList
        List<String> userNameList = new ArrayList<>();
        for(MessagesDocument message : messageList) {
            // 用户名称列表
            userNameList.add(message.getUserName());
        }
        // 保存消息信息
        messagesRepo.save(messageList);

        // 获取推送相关参数
        String appkey= ConfCenter.get("isj.push.properpushAppkey");
        String secureKey = ConfCenter.get("isj.push.properpushSecert");
        String pushUrl = ConfCenter.get("isj.push.pushUrl");
        PusherApp app = new PusherApp(pushUrl, appkey, secureKey);
        app.setAsync(true);
        // 拼接推送消息
        PushMessage msg = new PushMessage();
        msg.setTitle("掌上盛京医院");
        msg.setContent(content);
        msg.addCustomData("pageUrl", "messages");
        LOGGER.debug("Invoke saveMessage to push {} to {}", content, userNameList);
        // 推送消息
        app.pushMessageToUsers(msg, userNameList);
    }

    /**
     * 根据用户获取消息列表.
     *
     * @param userId 用户ID.
     * @return 消息列表.
     */
    @Override
    public List<MessagesDocument> findMessagesDocumentList(String userId) {
        return messagesRepo.findByUserIdOrderByCreateTimeDesc(userId);
    }

}
