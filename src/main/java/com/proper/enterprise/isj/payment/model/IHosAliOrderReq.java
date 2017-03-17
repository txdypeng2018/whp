package com.proper.enterprise.isj.payment.model;

import com.proper.enterprise.isj.support.VersionEnum;
import com.proper.enterprise.platform.pay.ali.model.AliOrderReq;

public class IHosAliOrderReq extends AliOrderReq implements OrderReq {

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
        return this.getTotalFee();
    }

}
