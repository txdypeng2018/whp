package com.proper.enterprise.isj.proxy.tasks;

import com.proper.enterprise.isj.pay.weixin.model.UnifiedNoticeRes;
import com.proper.enterprise.isj.pay.weixin.service.WeixinService;
import com.proper.enterprise.platform.core.utils.JSONUtil;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WeixinPayNotice2BusinessTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinPayNotice2BusinessTask.class);

    @Autowired
    private WeixinService weixinService;

    @Async
    public void run(UnifiedNoticeRes noticeRes) {
        String orderNum = null;
        try {
            if (noticeRes == null || StringUtil.isEmpty(noticeRes.getOutTradeNo())) {
                LOGGER.debug("微信异步通知参数解析异常,订单号:" + JSONUtil.toJSON(noticeRes));
            } else {
                orderNum = noticeRes.getOutTradeNo();
                weixinService.saveWeixinNoticeProcess(noticeRes);
            }
        } catch (Exception e) {
            LOGGER.debug("异步通知处理失败,订单号:" + orderNum, e);
        }
    }

}
