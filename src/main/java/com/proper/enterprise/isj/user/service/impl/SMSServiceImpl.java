package com.proper.enterprise.isj.user.service.impl;

import com.proper.enterprise.isj.log.service.WSLogService;
import com.proper.enterprise.isj.user.service.SMSService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.http.Callback;
import com.proper.enterprise.platform.core.utils.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class SMSServiceImpl implements SMSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSServiceImpl.class);

    @Value("${isj.sms.url}")
    private String url;

    @Value("${isj.sms.send}")
    private String template;

    @Autowired
    private WSLogService wsLogService;

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
        final Map<String, Object> param = new HashMap<>(2);
        param.put("phone", phone);
        param.put("msg", message);
        final long start = System.currentTimeMillis();
        try {
            final String data = MessageFormat.format(template, phone,
                    URLEncoder.encode(message, PEPConstants.DEFAULT_CHARSET.name()));
            HttpClient.post(url, MediaType.APPLICATION_FORM_URLENCODED, data, new Callback() {
                @Override
                public void onSuccess(ResponseEntity<byte[]> responseEntity) {
                    String resBody = new String(responseEntity.getBody(), PEPConstants.DEFAULT_CHARSET);
                    wsLogService.persistLog("SMS", param, url + "?" + data, resBody, System.currentTimeMillis() - start);
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
                    wsLogService.persistLog("SMS", param, "Exception occurs when sending SMS", ioe, System.currentTimeMillis() - start);
                    LOGGER.error("Exception occurs when sending SMS: phone({}), message({})", phone, message, ioe);
                }
            });
            result = true;
        } catch (UnsupportedEncodingException ioe) {
            wsLogService.persistLog("SMS", param, "Exception occurs when composing POST data", ioe, System.currentTimeMillis() - start);
            LOGGER.error("Exception occurs when composing POST data: phone({}), message({})", phone, message, ioe);
        }
        return result;
    }

}
