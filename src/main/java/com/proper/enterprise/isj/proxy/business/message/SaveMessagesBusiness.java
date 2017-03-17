package com.proper.enterprise.isj.proxy.business.message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MessageDocumentCollectionContext;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.repository.MessagesRepository;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.mobile.pushtools.PushMessage;
import com.proper.mobile.pushtools.PusherApp;

@Service
public class SaveMessagesBusiness<M extends MessageDocumentCollectionContext<Object>>
        implements IBusiness<Object, M>, ILoggable {

    @Autowired
    MessagesRepository messagesRepo;

    @Override
    public void process(M ctx) throws Exception {
        List<MessagesDocument> messageList = (List<MessagesDocument>) ctx.getMessagesDocuments();
        debug("Push info: {}", JSONUtil.toJSON(messageList));
        // 取得推送消息
        String content = messageList.get(0).getContent();
        // 取得userNameList
        List<String> userNameList = new ArrayList<>();
        for (MessagesDocument message : messageList) {
            // 用户名称列表
            userNameList.add(message.getUserName());
        }
        // 保存消息信息
        messagesRepo.save(messageList);

        // 获取推送相关参数
        String appkey = ConfCenter.get("isj.push.properpushAppkey");
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

}