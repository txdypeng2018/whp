package com.proper.enterprise.isj.proxy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.business.message.MessagesGetListBusiness;
import com.proper.enterprise.isj.proxy.document.MessagesDocument;

/**
 * 取得登录用户的消息列表.
 * Created by think on 2016/8/15 0015.
 */
@RestController
@RequestMapping(path = "/messages")
public class MessagesController extends IHosBaseController {

    /**
     * 取得登录用户的消息列表.
     * 
     * @return 登录用户的消息列表.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<MessagesDocument>> getMessages() throws Exception {
        return responseOfGet((List<MessagesDocument>) toolkit.execute(MessagesGetListBusiness.class, c -> {
        }));
    }
}
