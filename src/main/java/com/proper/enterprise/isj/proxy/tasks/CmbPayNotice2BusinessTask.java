package com.proper.enterprise.isj.proxy.tasks;

import com.proper.enterprise.isj.pay.cmb.controller.CmbPayController;
import com.proper.enterprise.isj.pay.cmb.service.CmbService;
import com.proper.enterprise.platform.core.utils.StringUtil;
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
        String threadName = Thread.currentThread().getName();
        if (StringUtil.isEmpty(threadName)) {
            LOGGER.debug("线程名称为空或者不合规则");
        } else {
            try {
                if (threadName.equals(CmbPayController.class.getName())) {
                    cmbNotice2Business(request);
                } else {
                    LOGGER.debug("未找到异步通知对应的实例");
                }
            } catch (Exception e) {
                LOGGER.debug("保存一网通异步通知异常:", e);
            }
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
