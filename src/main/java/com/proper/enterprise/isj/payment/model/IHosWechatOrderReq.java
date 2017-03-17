package com.proper.enterprise.isj.payment.model;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.pay.wechat.model.WechatOrderReq;

public class IHosWechatOrderReq extends WechatOrderReq implements OrderReq {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = VersionEnum.CODE_V1_0_0;

    @Override
    public String findOutTradeNo() {
        return this.getOutTradeNo();
    }

    @Override
    public String findTotalFee() {
        return String.valueOf(getTotalFee());
    }

}
