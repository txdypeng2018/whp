package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 退款接口请求Body对象
 */
@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundNoDupBodyReq implements Serializable {

    /**
     * 原订单日期YYYYMMDD
     */
    @XmlElement(name = "Date")
    private String date;

    /**
     * 订单号
     */
    @XmlElement(name = "BillNo")
    private String billNo;

    /**
     * 退款流水号
     */
    @XmlElement(name = "RefundNo")
    private String refundNo;

    /**
     * 退款金额
     */
    @XmlElement(name = "Amount")
    private String amount;

    /**
     * 退款备注。注意，如果包含特殊字符，需要转码。最终长度不能超过100字符
     */
    @XmlElement(name = "Desc")
    private String desc;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
