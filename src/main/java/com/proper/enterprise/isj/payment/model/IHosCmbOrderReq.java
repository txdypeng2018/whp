package com.proper.enterprise.isj.payment.model;

import com.proper.enterprise.platform.pay.cmb.model.CmbOrderReq;

public class IHosCmbOrderReq extends CmbOrderReq implements OrderReq {

    /**
     * serialVersionUID : long.
     */
    private static final long serialVersionUID = -4247929581345084160L;

    @Override
    public String findOutTradeNo() {
        return this.getBillNo();
    }

    @Override
    public String findTotalFee() {
        return this.getAmount();
    }

}