package com.proper.enterprise.isj.pay.weixin.model;


import com.proper.enterprise.platform.core.api.IBase;

public interface WeixinRefund  extends IBase {
    String getReturnCode();

    void setReturnCode(String returnCode);

    String getReturnMsg();

    void setReturnMsg(String returnMsg);

    String getResultCode();

    void setResultCode(String resultCode);

    String getErrCode();

    void setErrCode(String errCode);

    String getErrCodeDes();

    void setErrCodeDes(String errCodeDes);

    String getAppId();

    void setAppId(String appId);

    String getMchId();

    void setMchId(String mchId);

    String getDeviceInfo();

    void setDeviceInfo(String deviceInfo);

    String getNonceStr();

    void setNonceStr(String nonceStr);

    String getSign();

    void setSign(String sign);

    String getTransactionId();

    void setTransactionId(String transactionId);

    String getOutTradeNo();

    void setOutTradeNo(String outTradeNo);

    String getOutRefundNo();

    void setOutRefundNo(String outRefundNo);

    String getRefundId();

    void setRefundId(String refundId);

    String getRefundChannel();

    void setRefundChannel(String refundChannel);

    String getRefundFee();

    void setRefundFee(String refundFee);

    String getTotalFee();

    void setTotalFee(String totalFee);

    String getFeeType();

    void setFeeType(String feeType);

    String getCashFee();

    void setCashFee(String cashFee);

    String getCashRefundFee();

    void setCashRefundFee(String cashRefundFee);

    String getCouponRefundFee();

    void setCouponRefundFee(String couponRefundFee);

    String getCouponRefundCount();

    void setCouponRefundCount(String couponRefundCount);

    String getCouponRefundId();

    void setCouponRefundId(String couponRefundId);
}
