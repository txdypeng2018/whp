package com.proper.enterprise.isj.proxy.tasks;

import com.proper.enterprise.isj.pay.cmb.entity.CmbPayEntity;
import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 一网通异步通知
 */
@Component
public class CmbPayNotice2BusinessTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmbPayNotice2BusinessTask.class);

    private CmbService cmbService;

    private CmbPayEntity cmbInfo;

    public CmbPayEntity getCmbInfo() {
        return cmbInfo;
    }

    public void setCmbInfo(CmbPayEntity cmbInfo) {
        this.cmbInfo = cmbInfo;
    }

    public CmbService getCmbService() {
        return cmbService;
    }

    public void setCmbService(CmbService cmbService) {
        this.cmbService = cmbService;
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("启动线程CmbPayNotice2BusinessTask调用一网通异步通知业务处理开始");
            cmbNotice2Business(cmbInfo);
            LOGGER.debug("启动线程CmbPayNotice2BusinessTask调用一网通异步通知业务处理结束");
        } catch (Exception e) {
            LOGGER.debug("启动线程CmbPayNotice2BusinessTask调用一网通异步通知业务处理异常:", e);
        }
    }

    /**
     * 一网通异步通知处理业务
     *
     * @param cmbInfo 请求对象
     * @throws Exception
     */
    private void cmbNotice2Business(CmbPayEntity cmbInfo) throws Exception {
        cmbService.saveNoticePayInfo(cmbInfo);
    }

}
