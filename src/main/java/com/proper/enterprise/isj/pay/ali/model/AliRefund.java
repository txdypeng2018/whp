package com.proper.enterprise.isj.pay.ali.model;

/**
 * 支付宝退款
 */
 public interface AliRefund {

     String getSign();

     void setSign(String sign);

     String getTradeNo();

     void setTradeNo(String tradeNo);

     String getTotalFee();

     void setTotalFee(String totalFee);

     String getRefundResult();

     void setRefundResult(String refundResult);

     String getNotifyTime();

     void setNotifyTime(String notifyTime);

     String getSignType();

     void setSignType(String signType);

     String getNotifyType();

     void setNotifyType(String notifyType);

     String getNotifyId();

     void setNotifyId(String notifyId);

     String getBatchNo();

     void setBatchNo(String batchNo);

     String getSuccessNum();

     void setSuccessNum(String successNum);
}
