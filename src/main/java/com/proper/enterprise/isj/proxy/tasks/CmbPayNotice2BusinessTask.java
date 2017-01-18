package com.proper.enterprise.isj.proxy.tasks;

import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 一网通异步通知
 */
@Component
public class CmbPayNotice2BusinessTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmbPayNotice2BusinessTask.class);

    @Autowired
    CmbService cmbService;

    @Async
    public void run(CmbPayEntity cmbInfo) {
        try {
            LOGGER.debug("启动线程CmbPayNotice2BusinessTask调用一网通异步通知业务处理开始");
            cmbService.saveNoticePayInfo(cmbInfo);
            LOGGER.debug("启动线程CmbPayNotice2BusinessTask调用一网通异步通知业务处理结束");
        } catch (Exception e) {
            LOGGER.debug("启动线程CmbPayNotice2BusinessTask调用一网通异步通知业务处理异常:", e);
        }
    }

}
