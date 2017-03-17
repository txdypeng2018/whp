package com.proper.enterprise.isj.context;

import com.proper.enterprise.platform.core.api.BusinessContext;
import com.proper.enterprise.platform.pay.wechat.model.WechatOrderReq;

public interface WeChatOrderReqContext<T> extends BusinessContext<T> {
    void setWeChatReq(WechatOrderReq wechatReq);

    WechatOrderReq getWeChatReq();
}
