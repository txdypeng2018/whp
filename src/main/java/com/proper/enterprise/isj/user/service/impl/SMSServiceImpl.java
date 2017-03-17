package com.proper.enterprise.isj.user.service.impl;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.message.SendSMSFunction;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.isj.user.service.SMSService;

@Service
public class SMSServiceImpl extends AbstractService implements SMSService {

    /**
     * 异步调用发送手机短信接口
     * 不等待短信接口请求返回，调用后直接返回 true
     * 若在拼接请求发送的 data 时发生异常，则返回 false
     *
     * @param  phone   手机号
     * @param  message 消息内容
     * @return true：成功调用短信接口；false：调用短信接口失败
     */
    @Override
    public boolean sendSMS(final String phone, final String message) {
        return toolkit.executeFunction(SendSMSFunction.class, phone, message);
    }

}
