package com.proper.enterprise.isj.payment.model;

import com.proper.enterprise.platform.pay.wechat.model.WechatOrderReq;

public class IHosWechatOrderReq extends WechatOrderReq implements OrderReq {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = -2369573368118254598L;

    @Override
    public String findOutTradeNo() {
        return this.getOutTradeNo();
    }

    @Override
    public String findTotalFee() {
        return String.valueOf(getTotalFee());
    }

}
