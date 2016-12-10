package com.proper.enterprise.isj.pay.cmb.entity;

/**
 * 查询退款订单对象
 */
public class CmbQueryRefundEntity {

    /**
     * 订单号
     */
    private String billNo;

    /**
     * 订单日期yyyyMMdd
     */
    private String date;

    /**
     * 退款流水号
     */
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
