package com.proper.enterprise.isj.proxy.document.registration;

import com.proper.enterprise.platform.core.mongo.document.BaseDocument;

/**
 * Created by think on 2016/9/11 0011.
 */
public class RegistrationRefundReqDocument extends BaseDocument {

    /**
     * 医院ID 必填
     */
    private String hosId;

    /**
     * 平台订单号 必填
     */
    private String orderId;

    /**
     * 医院订单号 必填
     */
    private String hospOrderId;

    /**
     * 平台退款单号 必填
     */
    private String refundId;

    /**
     * 流水号（银行流水号、第三方支付流水号等）
     */
    private String refundSerialNum = "";

    /**
     * 总金额，单位：分 必填
     */
    private int totalFee;

    /**
     * 退款金额，单位：分 必填
     */
    private int refundFee;

    /**
     * 退款日期(银行、第三方支付等返回的结果码)，格式：YYYY-MM-DD
     */
    private String refundDate = "";

    /**
     * 退款时间(银行、第三方支付等返回的结果码)，格式：HH24:MI:SS
     */
    private String refundTime = "";

    /**
     * 交易响应代码(银行、第三方支付等返回的结果码)
     */
    private String refundResCode = "";

    /**
     * 交易响应描述
     */
    private String refundResDesc = "";

    /**
     * 退款原因
     */
    private String refundRemark = "";

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getHospOrderId() {
        return hospOrderId;
    }

    public void setHospOrderId(String hospOrderId) {
        this.hospOrderId = hospOrderId;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getRefundSerialNum() {
        return refundSerialNum;
    }

    public void setRefundSerialNum(String refundSerialNum) {
        this.refundSerialNum = refundSerialNum;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public int getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(int refundFee) {
        this.refundFee = refundFee;
    }

    public String getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(String refundDate) {
        this.refundDate = refundDate;
    }

    public String getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(String refundTime) {
        this.refundTime = refundTime;
    }

    public String getRefundResCode() {
        return refundResCode;
    }

    public void setRefundResCode(String refundResCode) {
        this.refundResCode = refundResCode;
    }

    public String getRefundResDesc() {
        return refundResDesc;
    }

    public void setRefundResDesc(String refundResDesc) {
        this.refundResDesc = refundResDesc;
    }

    public String getRefundRemark() {
        return refundRemark;
    }

    public void setRefundRemark(String refundRemark) {
        this.refundRemark = refundRemark;
    }
}
