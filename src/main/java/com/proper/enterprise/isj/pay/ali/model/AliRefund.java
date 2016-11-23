package com.proper.enterprise.isj.pay.ali.model;

/**
 * 支付宝退款
 */
public interface AliRefund {

    String getTradeNo();

    void setTradeNo(String tradeNo);

    String getOutTradeNo();

    void setOutTradeNo(String outTradeNo);

    String getOpenId();

    void setOpenId(String openId);

    String getBuyerLogonId();

    void setBuyerLogonId(String buyerLogonId);

    String getFundChange();

    void setFundChange(String fundChange);

    String getRefundFee();

    void setRefundFee(String refundFee);

    String getGmtRefundPay();

    void setGmtRefundPay(String gmtRefundPay);

    String getStoreName();

    void setStoreName(String storeName);

    String getBuyerUserId();

    void setBuyerUserId(String buyerUserId);

    String getSendBackFee();

    void setSendBackFee(String sendBackFee);
}
