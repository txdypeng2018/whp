package com.proper.enterprise.isj.pay.ali.model;

import java.io.Serializable;

public class AliRefundRes implements Serializable {

    private static final long serialVersionUID = -1l;

    private String code;

    private String msg;

    /**
     * 支付宝交易号 必填
     */
    private String tradeNo;

    /**
     * 商户订单号 必填
     */
    private String outTradeNo;

    /**
     * 买家支付宝用户号，该参数已废弃，请不要使用 必填
     */
    private String openId;

    /**
     * 用户的登录id 必填
     */
    private String buyerLogonId;

    /**
     * 本次退款是否发生了资金变化 必填
     */
    private String fundChange;

    /**
     * 退款总金额 必填
     */
    private String refundFee;

    /**
     * 退款支付时间 必填
     */
    private String gmtRefundPay;

    /**
     * 交易在支付时候的门店名称
     */
    private String storeName;

    /**
     * 买家在支付宝的用户id 必填
     */
    private String buyerUserId;

    /**
     * 本次商户实际退回金额
     */
    private String sendBackFee;

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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getBuyerLogonId() {
        return buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }

    public String getFundChange() {
        return fundChange;
    }

    public void setFundChange(String fundChange) {
        this.fundChange = fundChange;
    }

    public String getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(String refundFee) {
        this.refundFee = refundFee;
    }

    public String getGmtRefundPay() {
        return gmtRefundPay;
    }

    public void setGmtRefundPay(String gmtRefundPay) {
        this.gmtRefundPay = gmtRefundPay;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getSendBackFee() {
        return sendBackFee;
    }

    public void setSendBackFee(String sendBackFee) {
        this.sendBackFee = sendBackFee;
    }

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
}
