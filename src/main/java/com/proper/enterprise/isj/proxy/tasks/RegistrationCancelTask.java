package com.proper.enterprise.isj.proxy.tasks;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.enums.OrderCancelTypeEnum;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.platform.core.utils.DateUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

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

        List<RegistrationDocument> cancelRegRefundErrList = registrationService
                .findAlreadyCancelRegAndRefundErrRegList();
        for (RegistrationDocument registrationDocument : cancelRegRefundErrList) {
            if (!registrationDocument.getIsAppointment().equals("1")) {
                continue;
            }
            if (DateUtil.toDate(registrationDocument.getRegDate())
                    .compareTo(DateUtil.toDate(DateUtil.toDateString(new Date()))) <= 0) {
                continue;
            }
            if (StringUtil.isNotEmpty(registrationDocument.getRegistrationRefundReq().getOrderId())) {
                continue;
            }
            try {
                registrationService.saveCancelRegistration(registrationDocument.getId(),
                        OrderCancelTypeEnum.CANCEL_HANDLE);
            } catch (Exception e) {
                LOGGER.debug(
                        "对已交挂号费,手动退号,退费失败的记录进行退费,退费失败,单号:" + registrationDocument.getNum() + ",错误消息:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}