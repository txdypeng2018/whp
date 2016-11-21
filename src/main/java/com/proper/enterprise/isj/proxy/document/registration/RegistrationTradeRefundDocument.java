package com.proper.enterprise.isj.proxy.document.registration;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/10/5 0005.
 */
public class RegistrationTradeRefundDocument extends BaseDocument {

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 支付宝退款金额
     */
    private String refundAmount;

    /**
     * 支付宝退款单号
     */
    private String outRequestNo;

    /**
     * 微信商户退款单号
     */
    private String outRefundNo;

    /**
     * 总金额
     */
    private String totalFee;

    /**
     * 退款金额
     */
    private String refundFee;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getOutRequestNo() {
        return outRequestNo;
    }

    public void setOutRequestNo(String outRequestNo) {
        this.outRequestNo = outRequestNo;
    }

    public String getOutRefundNo() {
        return outRefundNo;
    }

    public void setOutRefundNo(String outRefundNo) {
        this.outRefundNo = outRefundNo;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(String refundFee) {
        this.refundFee = refundFee;
    }
}
