package com.proper.enterprise.isj.proxy.controller;

import java.util.ArrayList;
import java.util.List;

import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.platform.core.controller.BaseController;

/**
 * 取得登录用户的消息列表.
 * Created by think on 2016/8/15 0015.
 */
@RestController
@RequestMapping(path = "/messages")
public class MessagesController extends BaseController {

    @Autowired
    UserService userService;
    @Autowired
    MessagesService messagesService;

    /**
     * 取得登录用户的消息列表.
     * 
     * @return 登录用户的消息列表.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessagesDocument>> getMessages() throws Exception{
        // 获取当前用户
        User user = userService.getCurrentUser();
        List<MessagesDocument> msgList = new ArrayList<>();
        if(user != null) {
            // 设置用户ID
            String userId = user.getId();
            // 取得消息列表
            msgList = messagesService.findMessagesDocumentList(userId);
        }
        return responseOfGet(msgList);
    }
}
