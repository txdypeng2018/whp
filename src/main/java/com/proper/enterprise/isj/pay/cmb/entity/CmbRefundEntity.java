package com.proper.enterprise.isj.pay.cmb.entity;

import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 退款信息
 */
@Entity
@Table(name = "ISJ_CMB_REFUND_INFO")
@CacheEntity
public class CmbRefundEntity extends BaseEntity {

    /**
     * 退款状态: 0 : 成功
     *         -1 : 失败
     */
    private String refundCode;

    /**
     * 退款说明
     */
    private String refundMsg;

    //---------------请求信息---------------
    /**
     * 原订单号
     */
    private String reqBillNo;

    /**
     * 原订单日期YYYYMMDD
     */
    private String reqDate;

    /**
     * 退款流水号
     */
    private String reqRefundNo;

    /**
     * 退款金额
     */
    private String reqAmount;

    /**
     * 退款备注。
     */
    private String reqDesc;

    //---------------响应信息---------------
    /**
     * 银行退款流水号
     */
    private String refundNo;

    /**
     * 银行流水号
     */
    private String bankSeqNo;

    /**
     * 退款金额
     */
    private String amount;

    /**
     * 银行交易日期YYYYMMDD
     */
    private String date;

    /**
     * 银行交易时间hhmmss
     */
    private String time;

    public String getRefundCode() {
        return refundCode;
    }

    public void setRefundCode(String refundCode) {
        this.refundCode = refundCode;
    }

    public String getRefundMsg() {
        return refundMsg;
    }

    public void setRefundMsg(String refundMsg) {
        this.refundMsg = refundMsg;
    }

    public String getReqBillNo() {
        return reqBillNo;
    }

    public void setReqBillNo(String reqBillNo) {
        this.reqBillNo = reqBillNo;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getReqRefundNo() {
        return reqRefundNo;
    }

    public void setReqRefundNo(String reqRefundNo) {
        this.reqRefundNo = reqRefundNo;
    }

    public String getReqAmount() {
        return reqAmount;
    }

    public void setReqAmount(String reqAmount) {
        this.reqAmount = reqAmount;
    }

    public String getReqDesc() {
        return reqDesc;
    }

    public void setReqDesc(String reqDesc) {
        this.reqDesc = reqDesc;
    }

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
