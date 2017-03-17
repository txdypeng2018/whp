package com.proper.enterprise.isj.payment.service.impl;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.payment.WechatSaveNoticeProcessFunction;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.platform.api.pay.service.NoticeService;
import com.proper.enterprise.platform.pay.wechat.model.WechatNoticeRes;

/**
 * 微信异步通知业务处理类.
 */
@Service("pay_notice_wechat")
public class WechatNoticeServiceImpl extends AbstractService implements NoticeService<WechatNoticeRes> {

    /**
     * 异步通知业务处理代码
     *
     * @param noticeRes 微信异步通知处理参数
     */
    @Override
    public void saveNoticeProcess(WechatNoticeRes  noticeRes) {
        toolkit.executeFunction(WechatSaveNoticeProcessFunction.class, noticeRes);
    }
}
