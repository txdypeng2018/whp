package com.proper.enterprise.isj.pay.ali.model;

import java.io.Serializable;

/**
 * Created by think on 2016/10/5 0005.
 */
public class AliRefundTradeQueryRes implements Serializable {

    private static final long serialVersionUID = -1l;

    private String code;

    private String msg;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * 商家订单号
     */
    private String outTradeNo;

    /**
     * 本笔退款对应的退款请求号
     */
    private String outRequestNo;

    private String refundReason;

    private String totalAmount;

    private String refundAmount;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOutRequestNo() {
        return outRequestNo;
    }

    public void setOutRequestNo(String outRequestNo) {
        this.outRequestNo = outRequestNo;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }
}
