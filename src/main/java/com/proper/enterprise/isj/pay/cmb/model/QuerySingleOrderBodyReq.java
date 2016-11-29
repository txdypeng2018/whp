package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 单笔订单查询接口请求Body对象
 */
@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuerySingleOrderBodyReq implements Serializable {

    /**
     * 商户定单日期
     */
    @XmlElement(name = "Date")
    private String date;

    /**
     * 订单号
     */
    @XmlElement(name = "BillNo")
    private String billNo;


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
}
