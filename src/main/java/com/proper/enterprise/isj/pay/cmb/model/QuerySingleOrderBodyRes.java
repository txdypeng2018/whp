package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 单笔订单查询接口响应Body对象
 */
@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuerySingleOrderBodyRes {

    /**
     * 定单号
     */
    @XmlElement(name = "BillNo")
    private String billNo;

    /**
     * 定单金额
     */
    @XmlElement(name = "BillAmount")
    private String billAmount;

    /**
     * 实扣金额
     */
    @XmlElement(name = "Amount")
    private String amount;

    /**
     * 受理日期
     */
    @XmlElement(name = "AcceptDate")
    private String acceptDate;

    /**
     * 受理时间
     */
    @XmlElement(name = "AcceptTime")
    private String acceptTime;

    /**
     * 定单状态
     */
    @XmlElement(name = "Status")
    private String status;

    /**
     * 卡类型
     */
    @XmlElement(name = "CardType")
    private String cardType;

    /**
     * 卡号（部分数字用“*”掩盖）
     */
    @XmlElement(name = "CardNo")
    private String cardNo;

    /**
     * 手续费
     */
    @XmlElement(name = "Fee")
    private String fee;

    /**
     * 银行流水号
     */
    @XmlElement(name = "BankSeqNo")
    private String bankSeqNo;

    /**
     * 商户自定义参数。里面的特殊字符已经转码
     */
    @XmlElement(name = "MerchantPara")
    private String merchantPara;

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(String acceptDate) {
        this.acceptDate = acceptDate;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getBankSeqNo() {
        return bankSeqNo;
    }

    public void setBankSeqNo(String bankSeqNo) {
        this.bankSeqNo = bankSeqNo;
    }

    public String getMerchantPara() {
        return merchantPara;
    }

    public void setMerchantPara(String merchantPara) {
        this.merchantPara = merchantPara;
    }
}
