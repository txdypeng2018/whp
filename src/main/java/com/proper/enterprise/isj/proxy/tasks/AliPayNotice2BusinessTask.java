package com.proper.enterprise.isj.proxy.tasks;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proper.enterprise.isj.pay.ali.controller.AliPayController;
import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.platform.core.utils.JSONUtil;

/**
 * Created by think on 2016/12/22 0022.
 */
public class AliPayNotice2BusinessTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayController.class);

    private Map<String, String> aliParamMap;

    private AliService aliService;

    public void setAliParamMap(Map<String, String> aliParamMap) {
        this.aliParamMap = aliParamMap;
    }

    public void setAliService(AliService aliService) {
        this.aliService = aliService;
    }


    @Override
    public void run() {
        String orderNum = null;
        try {
            if (aliParamMap == null || !aliParamMap.containsKey("out_trade_no")) {
                LOGGER.debug("支付宝异步通知参数解析异常,订单号:" + JSONUtil.toJSON(aliParamMap));
            } else {
                orderNum = aliParamMap.get("out_trade_no");
                aliNotice2Business(orderNum);
            }
        } catch (Exception e) {
            LOGGER.debug("异步通知处理失败,订单号:" + orderNum, e);
            // saveNoticeErrRefund(orderNum, e);
        }
    }

    /**
     * 支付宝异步通知处理业务
     *
     * @param orderNum
     * @throws Exception
     */
    private void aliNotice2Business(String orderNum) throws Exception {
        aliService.saveAliNoticeProcess(orderNum, aliParamMap, "pay");
    }

}
