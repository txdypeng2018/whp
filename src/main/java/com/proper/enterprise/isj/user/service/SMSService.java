package com.proper.enterprise.isj.user.service;

public interface SMSService {

    /**
     * 发送短信.
     *
     * @param  phone   手机号.
     * @param  message 消息内容.
     * @return 是否发送成功.
     */
    boolean sendSMS(String phone, String message);

}
