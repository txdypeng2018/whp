package com.proper.enterprise.isj.pay.ali.service;

import java.util.Map;

import com.proper.enterprise.isj.pay.ali.model.*;

/**
 * 微信支付服务接口
 *
 */
public interface AliService {

    Ali save(Ali ali);

    AliRefund save(AliRefund aliRefund);

    Ali findByOutTradeNo(String outTradeNo);

    AliRefund findByTradeNo(String tradeNo);

    boolean saveAliNoticeProcess(String orderNo, Map<String, String> params, String dealType) throws Exception;

    boolean saveAliRefundProcess(Map<String, String> params) throws Exception;

    boolean saveRefundProcess(AliRefundDetailReq reDetailReq) throws Exception;

    <T> String getOrderInfo(T t, Class<T> clz) throws Exception;

    <T> String getRefundInfo(T t, Class<T> clz, boolean isURLEncoder) throws Exception;

    boolean verify(Map<String, String> params) throws Exception;

    AliPayTradeQueryRes getAliPayTradeQueryRes(String outTradeNo);

    AliRefundTradeQueryRes getAliRefundTradeQueryRes(String outTradeNo, String outRequestNo);

    /**
     * 发起线下退款
     * 
     * @param outTradeNo
     *            订单号
     * @param refundNo
     *            退款单号
     * @param amount
     *            退款金额
     * @return
     */
    AliRefundRes saveAliRefundResProcess(String outTradeNo, String refundNo, String amount, String refundReason);

}
