package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 按订单号查询退款订单接口请求Body对象
 */
@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryRefundBodyReq  implements Serializable {

    /**
     * 订单号
     */
    @XmlElement(name = "BillNo")
    private String billNo;

    /**
     * 商户定单日期
     */
    @XmlElement(name = "Date")
    private String date;

    /**
     * 退款流水号
     */
    @XmlElement(name = "RefundNo")
    private String refundNo;


    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }
}
