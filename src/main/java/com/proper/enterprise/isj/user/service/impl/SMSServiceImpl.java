package com.proper.enterprise.isj.user.service.impl;

import com.proper.enterprise.isj.user.service.SMSService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.http.Callback;
import com.proper.enterprise.platform.core.utils.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

@Service
public class SMSServiceImpl implements SMSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSServiceImpl.class);

    @Value("${isj.sms.url}")
    String url;

    @Value("${isj.sms.send}")
    String template;

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
        boolean result = false;
        try {
            final String data = MessageFormat.format(template, phone,
                    URLEncoder.encode(message, PEPConstants.DEFAULT_CHARSET.name()));
            HttpClient.post(url, MediaType.APPLICATION_FORM_URLENCODED, data, new Callback() {
                @Override
                public void onSuccess(ResponseEntity<byte[]> responseEntity) {
                    String resBody = new String(responseEntity.getBody(), PEPConstants.DEFAULT_CHARSET);
                    if (resBody.startsWith("0:")) {
                        LOGGER.trace("Send sms ({}) successfully. Status code: {}, Response body: {}",
                                data, responseEntity.getStatusCode(), resBody);
                    } else {
                        LOGGER.error("Send sms (POST: {}, data: {}) FAILED! Status code: {}, Response body: {}",
                                url, data, responseEntity.getStatusCode(), resBody);
                    }
                }

                @Override
                public void onError(IOException ioe) {
                    LOGGER.error("Exception occurs when composing POST data: phone({}), message({})", phone, message, ioe);
                }
            });
            result = true;
        } catch (UnsupportedEncodingException ioe) {
            LOGGER.error("Exception occurs when composing POST data: phone({}), message({})", phone, message, ioe);
        }
        return result;
    }

}
