package com.proper.enterprise.isj.payment.service.impl;

import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.function.payment.FetchPayTimeResFunction;
import com.proper.enterprise.isj.function.payment.SaveCheckOrderFunction;
import com.proper.enterprise.isj.payment.service.BusinessPayService;
import com.proper.enterprise.isj.support.service.AbstractService;
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;
import com.proper.enterprise.platform.api.pay.model.PayResultRes;
import com.proper.enterprise.platform.api.pay.model.PrepayReq;

/**
 * 支付ServiceImpl.
 */
@Service
public class BusinessPayServiceImpl extends AbstractService implements BusinessPayService {

    /**
     * 取得支付时间时效对象.
     *
     * @param outTradeNo 订单号.
     * @param prepayReq 预支付对象.
     * @return 处理结果
     */
    @Override
    public PayResultRes getPayTimeRes(String outTradeNo, PrepayReq prepayReq) {
        return toolkit.executeFunction(FetchPayTimeResFunction.class, outTradeNo, prepayReq);
    }

    /**
     * 预支付订单校验.
     *
     * @param payChannel 支付渠道.
     * @param outTradeNo 订单号.
     * @param totalFee 支付金额(以分为单位).
     * @return 校验结果对象
     */
    @Override
    public PayResultRes saveCheckOrder(PayChannel payChannel, String outTradeNo, String totalFee) {
        return toolkit.executeFunction(SaveCheckOrderFunction.class, payChannel, outTradeNo, totalFee);
    }
}
