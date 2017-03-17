package com.proper.enterprise.isj.function.message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.log.service.WSLogService;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.http.Callback;
import com.proper.enterprise.platform.core.utils.http.HttpClient;

@Service
public class SendSMSFunction implements IFunction<Object>, ILoggable{

    @Value("${isj.sms.url}")
    private String url;

    @Value("${isj.sms.send}")
    private String template;

    @Autowired
    private WSLogService wsLogService;
    
    @Override
    public Object execute(Object... params) throws Exception {
        return sendSMS((String) params[0], (String) params[1]);
    }

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
                        trace("Send sms ({}) successfully. Status code: {}, Response body: {}",
                                data, responseEntity.getStatusCode(), resBody);
                    } else {
                        error("Send sms (POST: {}, data: {}) FAILED! Status code: {}, Response body: {}",
                                url, data, responseEntity.getStatusCode(), resBody);
                    }
                }

                @Override
                public void onError(IOException ioe) {
                    wsLogService.persistLog("SMS", param, "Exception occurs when sending SMS", ioe, System.currentTimeMillis() - start);
                    error("Exception occurs when sending SMS: phone({}), message({})", phone, message, ioe);
                }
            });
            result = true;
        } catch (UnsupportedEncodingException ioe) {
            wsLogService.persistLog("SMS", param, "Exception occurs when composing POST data", ioe, System.currentTimeMillis() - start);
            error("Exception occurs when composing POST data: phone({}), message({})", phone, message, ioe);
        }
        return result;
    }
}
