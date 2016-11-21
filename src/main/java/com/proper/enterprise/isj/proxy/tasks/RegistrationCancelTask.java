package com.proper.enterprise.isj.proxy.tasks;

import java.util.*;

import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;

/**
 * Created by think on 2016/9/29 0029.
 */
@Component
public class RegistrationCancelTask implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(RegistrationCancelTask.class);

    @Autowired
    RegistrationService registrationService;

    @Override
    public void run() {
        List<RegistrationDocument> overTimeList = registrationService.findOverTimeRegistrationDocumentList();
        for (RegistrationDocument registrationDocument : overTimeList) {
            try {
                registrationService.saveCancelRegistration(registrationDocument.getId(),
                        OrderCancelTypeEnum.CANCEL_OVERTIME);
            } catch (Exception e) {
                LOGGER.debug("超时自动退号失败,挂号单号:" + registrationDocument.getNum() + ",错误消息:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
