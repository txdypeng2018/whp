package com.proper.enterprise.isj.proxy.document;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 推送消息
 */
@Document(collection = "messages_info")
public class MessagesDocument extends BaseDocument {

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户Name
     */
    private String userName;
    /**
     * 消息时间,格式:yyyy-MM-dd hh24:mi
     */
    private String date;
    /**
     * 消息内容
     */
    private String content;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
