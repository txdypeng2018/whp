package com.proper.enterprise.isj.pay.ali.model;

public class AliRefundDetailReq {

    /**
     * 交易号
     */
    private String tradeNo;

    /**
     * 退款批次号
     */
    private String batchNo;

    /**
     * 退款金额
     */
    private String totalFee;

    /**
     * 退款理由
     */
    private String refundeReason = "协商退款";

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getRefundeReason() {
        return refundeReason;
    }

    public void setRefundeReason(String refundeReason) {
        this.refundeReason = refundeReason;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
}
