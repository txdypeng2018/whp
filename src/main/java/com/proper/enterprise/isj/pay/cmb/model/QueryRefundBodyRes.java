package com.proper.enterprise.isj.pay.cmb.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 按订单号查询退款订单接口响应Body对象
 */
@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryRefundBodyRes {

    /**
     * 订单记录列表BllRecord
     */
    @XmlElement(name = "BllRecord")
    private List<BillRecordRes> billRecord;

    public List<BillRecordRes> getBillRecord() {
        return billRecord;
    }

    public void setBillRecord(List<BillRecordRes> billRecord) {
        this.billRecord = billRecord;
    }
}
