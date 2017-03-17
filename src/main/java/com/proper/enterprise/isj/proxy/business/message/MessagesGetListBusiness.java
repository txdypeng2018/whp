package com.proper.enterprise.isj.proxy.business.message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;

@Service
public class MessagesGetListBusiness<M extends ModifiedResultBusinessContext<Object>> implements IBusiness<Object, M> {
    @Autowired
    UserService userService;

    @Autowired
    MessagesService messagesService;

    @Override
    public void process(M ctx) throws Exception {
        // 获取当前用户
        User user = userService.getCurrentUser();
        List<MessagesDocument> msgList = new ArrayList<>();
        if (user != null) {
            // 设置用户ID
            String userId = user.getId();
            // 取得消息列表
            msgList = messagesService.findMessagesDocumentList(userId);
        }
        ctx.setResult(msgList);
    }

}
