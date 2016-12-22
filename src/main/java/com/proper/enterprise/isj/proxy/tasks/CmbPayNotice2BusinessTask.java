package com.proper.enterprise.isj.proxy.tasks;

import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 一网通异步通知
 */
@Component
public class CmbPayNotice2BusinessTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmbPayNotice2BusinessTask.class);

    private HttpServletRequest request;

    private CmbService cmbService;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
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
            cmbNotice2Business(request);
            LOGGER.debug("启动线程CmbPayNotice2BusinessTask调用一网通异步通知业务处理结束");
        } catch (Exception e) {
            LOGGER.debug("启动线程CmbPayNotice2BusinessTask调用一网通异步通知业务处理异常:", e);
        }
    }

    /**
     * 一网通异步通知处理业务
     *
     * @param request 请求
     * @throws Exception
     */
    private void cmbNotice2Business(HttpServletRequest request) throws Exception {
        cmbService.saveNoticePayInfo(request);
    }

}
