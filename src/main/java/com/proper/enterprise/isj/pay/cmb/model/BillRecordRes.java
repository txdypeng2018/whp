package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 订单信息
 */
@XmlRootElement(name = "BllRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillRecordRes {

    /**
     * 商户分行号
     */
    @XmlElement(name = "BranchNo")
    private String branchNo;

    /**
     * 商户号
     */
    @XmlElement(name = "MerchantNo")
    private String merchantNo;

    /**
     * 商户定单号
     */
    @XmlElement(name = "BillNo")
    private String billNo;

    /**
     * 商户订单日期yyyyMMdd
     */
    @XmlElement(name = "MchBillDat")
    private String mchBillDat;

    /**
     * 订单状态
     */
    @XmlElement(name = "BillState")
    private String billState;

    /**
     * 退款单流水号
     */
    @XmlElement(name = "RefundNo")
    private String refundNo;

    /**
     * 退款金额
     */
    @XmlElement(name = "Amount")
    private String amount;

    /**
     * 退款币种
     */
    @XmlElement(name = "RefundCurrency")
    private String refundCurrency;

    /**
     * 费用金额
     */
    @XmlElement(name = "FeeAmount")
    private String feeAmount;

    /**
     * 银行受理日期yyyyMMdd
     */
    @XmlElement(name = "BankDate")
    private String bankDate;

    /**
     * 银行受理时间
     */
    @XmlElement(name = "BankTime")
    private String bankTime;

    /**
     * 退款单流水号（收单主机生成）
     */
    @XmlElement(name = "BankSeqNo")
    private String bankSeqNo;

    /**
     * 定单流水号（收单主机生成）
     */
    @XmlElement(name = "BankBillNo")
    private String bankBillNo;

    /**
     * 经办操作员号
     */
    @XmlElement(name = "AplOprator")
    private String aplOprator;

    /**
     * 经办日期
     */
    @XmlElement(name = "AplDate")
    private String aplDate;

    /**
     * 经办时间
     */
    @XmlElement(name = "AplTime")
    private String aplTime;

    /**
     * 授权操作员号
     */
    @XmlElement(name = "AutOprator")
    private String autOprator;

    /**
     * 授权处理日期
     */
    @XmlElement(name = "StlDate")
    private String stlDate;

    /**
     * 授权处理时间
     */
    @XmlElement(name = "StlTime")
    private String stlTime;

    public String getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(String branchNo) {
        this.branchNo = branchNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getMchBillDat() {
        return mchBillDat;
    }

    public void setMchBillDat(String mchBillDat) {
        this.mchBillDat = mchBillDat;
    }

    public String getBillState() {
        return billState;
    }

    public void setBillState(String billState) {
        this.billState = billState;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRefundCurrency() {
        return refundCurrency;
    }

    public void setRefundCurrency(String refundCurrency) {
        this.refundCurrency = refundCurrency;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getBankDate() {
        return bankDate;
    }

    public void setBankDate(String bankDate) {
        this.bankDate = bankDate;
    }

    public String getBankTime() {
        return bankTime;
    }

    public void setBankTime(String bankTime) {
        this.bankTime = bankTime;
    }

    public String getBankSeqNo() {
        return bankSeqNo;
    }

    public void setBankSeqNo(String bankSeqNo) {
        this.bankSeqNo = bankSeqNo;
    }

    public String getBankBillNo() {
        return bankBillNo;
    }

    public void setBankBillNo(String bankBillNo) {
        this.bankBillNo = bankBillNo;
    }

    public String getAplOprator() {
        return aplOprator;
    }

    public void setAplOprator(String aplOprator) {
        this.aplOprator = aplOprator;
    }

    public String getAplDate() {
        return aplDate;
    }

    public void setAplDate(String aplDate) {
        this.aplDate = aplDate;
    }

    public String getAplTime() {
        return aplTime;
    }

    public void setAplTime(String aplTime) {
        this.aplTime = aplTime;
    }

    public String getAutOprator() {
        return autOprator;
    }

    public void setAutOprator(String autOprator) {
        this.autOprator = autOprator;
    }

    public String getStlDate() {
        return stlDate;
    }

    public void setStlDate(String stlDate) {
        this.stlDate = stlDate;
    }

    public String getStlTime() {
        return stlTime;
    }

    public void setStlTime(String stlTime) {
        this.stlTime = stlTime;
    }
}
