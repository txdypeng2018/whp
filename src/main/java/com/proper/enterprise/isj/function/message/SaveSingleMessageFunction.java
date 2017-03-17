package com.proper.enterprise.isj.function.message;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.repository.MessagesRepository;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.mobile.pushtools.PushMessage;
import com.proper.mobile.pushtools.PusherApp;

@Service
public class SaveSingleMessageFunction implements IFunction<Object>, ILoggable {

    @Autowired
    MessagesRepository messagesRepo;

    @Override
    public Object execute(Object... params) throws Exception {
        saveMessage((MessagesDocument) params[0]);
        return null;
    }

    public void saveMessage(MessagesDocument messageDocument) {
        
        try {
            debug("Push info: {}", JSONUtil.toJSON(messageDocument));
        } catch (IOException e) {
            info(e.getMessage(), e);
        }
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
        debug("Invoke saveMessage to push {} to {}", messageDocument.getContent(), messageDocument.getUserName());
        // 推送消息
        app.pushMessageToOneUser(msg, messageDocument.getUserName());
        
    }

}
