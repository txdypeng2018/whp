package com.proper.enterprise.isj.payment.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.MapParamsContext;
import com.proper.enterprise.isj.payment.business.AliNoticePayOrRegisterBusiness;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.platform.api.pay.service.NoticeService;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.utils.JSONUtil;

/**
 * 支付宝异步通知业务处理类.
 */
@Service("pay_notice_ali")
public class AliNoticeServiceImpl extends AbstractService implements NoticeService<Map<String, String>>, ILoggable {

    /**
     * 异步通知业务处理代码
     *
     * @param params
     *            支付宝异步通知处理参数
     */
    @SuppressWarnings("unchecked")
    @Override
    public void saveNoticeProcess(Map<String, String> params) {
        debug("-------------支付宝异步通知相关业务处理--------处理开始---------");
        try {
            if (params == null || !params.containsKey("out_trade_no")) {
                debug("支付宝异步通知参数解析异常:{}", JSONUtil.toJSON(params));
            } else {
                toolkit.execute(AliNoticePayOrRegisterBusiness.class,
                        ctx -> ((MapParamsContext<Object>) ctx).setParams(params));
            }
            debug("-------------支付宝异步通知相关业务处理-----正常结束------------");
        } catch (Exception e) {
            debug("支付宝异步通知业务处理异常:{}", e);
        }
    }
}
