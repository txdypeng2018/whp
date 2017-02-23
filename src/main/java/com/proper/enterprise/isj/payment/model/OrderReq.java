package com.proper.enterprise.isj.payment.model;

import java.io.Serializable;

public interface OrderReq extends Serializable {

    String findOutTradeNo();

    String findTotalFee();

}
