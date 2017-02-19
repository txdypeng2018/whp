package com.proper.enterprise.isj.payment.service;

import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.pay.model.PayResultRes;
import com.proper.enterprise.platform.api.pay.model.PrepayReq;

/**
 * 支付Service.
 */
public interface BusinessPayService {

    /**
     * 取得支付时间时效对象.
     *
     * @param outTradeNo 订单号.
     * @param prepayReq 预支付对象.
     * @return 处理结果
     */
    PayResultRes getPayTimeRes(String outTradeNo, PrepayReq prepayReq);

    /**
     * 预支付订单校验.
     *
     * @param payChannel 支付渠道.
     * @param outTradeNo 订单号.
     * @param totalFee 支付金额(以分为单位).
     * @return 校验结果对象
     */
    PayResultRes saveCheckOrder(PayChannel payChannel, String outTradeNo, String totalFee);
}
