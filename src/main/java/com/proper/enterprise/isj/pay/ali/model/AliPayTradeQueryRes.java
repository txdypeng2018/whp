package com.proper.enterprise.isj.pay.ali.model;

import java.io.Serializable;

/**
 * Created by think on 2016/10/4 0004. 查询接口文档地址
 * https://doc.open.alipay.com/docs/api.htm?spm=a219a.7629065.0.0.IR5LMA&apiId=
 * 757&docType=4
 */
public class AliPayTradeQueryRes implements Serializable {

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
     * 买家支付宝账号
     */
    private String buyerLogonId;

    /**
     * 交易状态： WAIT_BUYER_PAY（交易创建，等待买家付款）、 TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
     * TRADE_SUCCESS（交易支付成功）、 TRADE_FINISHED（交易结束，不可退款）
     */
    private String tradeStatus;

    /**
     * 交易的订单金额，单位为元，两位小数。
     */
    private String totalAmount;
    /**
     * 实收金额，单位为元，两位小数。
     */
    private String receiptAmount;

    /**
     * 本次交易打款给卖家的时间
     */
    private String sendPayDate;

    /**
     * 买家在支付宝的用户id
     */
    private String buyerUserId;


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

    public String getBuyerLogonId() {
        return buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(String receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public String getSendPayDate() {
        return sendPayDate;
    }

    public void setSendPayDate(String sendPayDate) {
        this.sendPayDate = sendPayDate;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }
}
