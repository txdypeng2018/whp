package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 退款接口响应Body对象
 */
@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundNoDupBodyRes {

    /**
     * 银行退款流水号
     */
    @XmlElement(name = "RefundNo")
    private String refundNo;

    /**
     * 银行流水号
     */
    @XmlElement(name = "BankSeqNo")
    private String bankSeqNo;

    /**
     * 退款金额
     */
    @XmlElement(name = "Amount")
    private String amount;

    /**
     * 银行交易日期YYYYMMDD
     */
    @XmlElement(name = "Date")
    private String date;

    /**

     * 银行交易时间hhmmss
     */
    @XmlElement(name = "Time")
    private String time;

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getBankSeqNo() {
        return bankSeqNo;
    }

    public void setBankSeqNo(String bankSeqNo) {
        this.bankSeqNo = bankSeqNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
