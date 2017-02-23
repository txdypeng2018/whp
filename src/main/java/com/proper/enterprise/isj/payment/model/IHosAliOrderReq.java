package com.proper.enterprise.isj.payment.model;

import com.proper.enterprise.platform.pay.ali.model.AliOrderReq;

public class IHosAliOrderReq extends AliOrderReq implements OrderReq {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = -5545351356866176891L;

    @Override
    public String findOutTradeNo() {
        return this.getOutTradeNo();
    }

    @Override
    public String findTotalFee() {
        return this.getTotalFee();
    }

}
