package com.proper.enterprise.isj.proxy.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proper.enterprise.isj.pay.ali.controller.AliPayController;
import com.proper.enterprise.isj.pay.weixin.model.UnifiedNoticeRes;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/12/22 0022.
 */
public class WeixinPayNotice2BusinessTask implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayController.class);


    private UnifiedNoticeRes noticeRes;

    private WeixinService weixinService;


    public void setNoticeRes(UnifiedNoticeRes noticeRes) {
        this.noticeRes = noticeRes;
    }

    public void setWeixinService(WeixinService weixinService) {
        this.weixinService = weixinService;
    }

    @Override
    public void run() {
        String orderNum = null;
        try {
            if (noticeRes == null || StringUtil.isEmpty(noticeRes.getOutTradeNo())) {
                LOGGER.debug("微信异步通知参数解析异常,订单号:" + JSONUtil.toJSON(noticeRes));
            } else {
                orderNum = noticeRes.getOutTradeNo();
                weixinNotice2Business(orderNum);
            }
        } catch (Exception e) {
            LOGGER.debug("异步通知处理失败,订单号:" + orderNum, e);
            // saveNoticeErrRefund(orderNum, e);
        }
    }


    /**
     * 微信异步通知处理业务
     *
     * @param orderNum
     * @throws Exception
     */
    private void weixinNotice2Business(String orderNum) throws Exception {
        weixinService.saveWeixinNoticeProcess(noticeRes);
    }
}
