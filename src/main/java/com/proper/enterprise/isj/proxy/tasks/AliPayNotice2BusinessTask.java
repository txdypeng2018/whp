package com.proper.enterprise.isj.proxy.tasks;

import com.proper.enterprise.isj.pay.ali.service.AliService;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AliPayNotice2BusinessTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliPayNotice2BusinessTask.class);

    @Autowired
    AliService aliService;

    @Async
    public void run(Map<String, String> aliParamMap, String dealType) {
        String orderNum = null;
        try {
            if (aliParamMap == null || !aliParamMap.containsKey("out_trade_no")) {
                LOGGER.debug("支付宝异步通知参数解析异常,订单号:" + JSONUtil.toJSON(aliParamMap));
            } else {
                orderNum = aliParamMap.get("out_trade_no");
                aliService.saveAliNoticeProcess(orderNum, aliParamMap, dealType);
            }
        } catch (Exception e) {
            LOGGER.debug("异步通知处理失败,订单号:" + orderNum, e);
        }
    }

}
