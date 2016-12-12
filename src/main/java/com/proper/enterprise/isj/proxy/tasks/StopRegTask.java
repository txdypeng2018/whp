package com.proper.enterprise.isj.proxy.tasks;

import java.util.*;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.document.MessagesDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.document.StopRegRecordDocument;
import com.proper.enterprise.isj.proxy.enums.RegistrationStatusEnum;
import com.proper.enterprise.isj.proxy.enums.SendPushMsgEnum;
import com.proper.enterprise.isj.proxy.service.MessagesService;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.proxy.service.StopRegRecordService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.WebServicesClient;
import com.proper.enterprise.isj.webservices.model.res.ResModel;
import com.proper.enterprise.isj.webservices.model.res.StopRegInfo;
import com.proper.enterprise.isj.webservices.model.res.stopreg.StopReg;
import com.proper.enterprise.platform.core.utils.DateUtil;

/**
 * Created by think on 2016/10/6 0006.
 */
@Component
public class StopRegTask implements Runnable {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StopRegTask.class);
    @Autowired
    @Lazy
    WebServicesClient webServicesClient;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    MessagesService messagesService;

    @Autowired
    StopRegRecordService stopRegRecordService;


    @Override
    public void run() {
        ResModel<StopRegInfo> res = null;
        try {
            res = webServicesClient.stopReg();
        } catch (Exception e) {
            LOGGER.debug("调用HIS获取停诊信息失败", e);
        }
        try {
            if (res != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Set<String> regKeySet = new HashSet<>();
                List<StopRegRecordDocument> oldStopRegList = stopRegRecordService
                        .findStopRegRecordDocument(DateUtil.toDateString(cal.getTime()));
                for (StopRegRecordDocument stopRegRecordDocument : oldStopRegList) {
                    regKeySet.add(stopRegRecordDocument.getStopReg());
                }
                cal.add(Calendar.DAY_OF_MONTH, CenterFunctionUtils.SCHEDULING_MAXADD_DAY + 2);
                List<StopReg> stopReglist = res.getRes().getStopRegList();
                String regDate = null;
                String beginTime = null;
                String endTime = null;
                String doctorId = null;
                String deptId = null;
                StringBuilder regKey = null;
                Map<String, String> paramMap = null;
                List<RegistrationDocument> regList = null;
                for (StopReg stopReg : stopReglist) {
                    doctorId = stopReg.getDoctorId();
                    deptId = stopReg.getDeptId();
                    regDate = DateUtil.toDateString(DateUtil.toDate(stopReg.getRegDate().split(" ")[0]));
                    if (DateUtil.toDate(regDate).compareTo(cal.getTime()) > 0) {
                        continue;
                    }
                    beginTime = DateUtil.toString(DateUtil.toDate(stopReg.getBeginTime().split(" ")[1], "HH:mm:ss"),
                            "HH:mm");
                    endTime = DateUtil.toString(DateUtil.toDate(stopReg.getEndTime().split(" ")[1], "HH:mm:ss"),
                            "HH:mm");
                    regKey = new StringBuilder();
                    regKey.append(doctorId).append("_").append(deptId).append("_").append(regDate).append("_")
                            .append(beginTime).append("_").append(endTime);
                    if (regKeySet.contains(regKey.toString())) {
                        continue;
                    }
                    paramMap = new HashMap<>();
                    paramMap.put("doctorId", doctorId);
                    paramMap.put("deptId", deptId);
                    paramMap.put("regDate", regDate);
                    paramMap.put("beginTime", beginTime);
                    paramMap.put("endTime", endTime);

                    regList = registrationService.findRegistrationDocumentByStopReg(paramMap);
                    StopRegRecordDocument record = stopRegRecordService.getByStopReg(regKey.toString());
                    if (record == null) {
                        record = new StopRegRecordDocument();
                        record.setStopDate(regDate);
                        record.setStopReg(regKey.toString());
                        record.setNoticeMsgNum(regList.size());
                        stopRegRecordService.saveStopRegRecord(record);
                    }
                    for (RegistrationDocument registrationDocument : regList) {
                        registrationDocument.setStatusCode(RegistrationStatusEnum.SUSPEND_MED.getValue());
                        registrationDocument.setStatus(
                                CenterFunctionUtils.getRegistrationStatusName(registrationDocument.getStatusCode()));
                        registrationService.saveRegistrationDocument(registrationDocument);
                        registrationDocument.setRegDate(regDate);
                        registrationDocument.setBeginTime(beginTime);
                        registrationDocument.setEndTime(endTime);
                        this.sendRegistrationMsg(registrationDocument);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("停诊信息报错", e);
        }
    }

    private void sendRegistrationMsg(RegistrationDocument updateReg) throws Exception {
        MessagesDocument regMsg = new MessagesDocument();
        regMsg.setContent(CenterFunctionUtils.getPushMsgContent(SendPushMsgEnum.STOP_REG_PLATFORM, updateReg));
        regMsg.setDate(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm"));
        regMsg.setUserId(updateReg.getCreateUserId());
        regMsg.setUserName(updateReg.getOperatorPhone());
        messagesService.saveMessage(regMsg);
    }
}