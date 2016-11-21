package com.proper.enterprise.isj.webservices.model.req;

import com.proper.enterprise.isj.webservices.model.enmus.PayChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayOrderReq {

    /**
     * 医院ID 必填
     */
    private String hosId;

    /**
     * 平台订单号 必填
     */
    private String orderId;

    /**
     * HIS就诊登记号，缴费单唯一ID 必填
     */
    private String hospClinicCode;


    /**
     * 缴费流水号，缴费单唯一ID 必填
     */
    private String hospSequence;

    /**
     * 流水号（银行流水号、第三方支付流水号等） 必填
     */
    private String serialNum;

    /**
     * 交易日期（银行、第三方支付等），格式：YYYY-MM-DD 必填
     */
    private String payDate;

    /**
     * 交易时间（银行、第三方支付等），格式：HH24:MI:SS 必填
     */
    private String payTime;

    /**
     * 支付渠道ID，详见 “支付渠道“ 必填
     */
    private PayChannel payChannelId;

    /**
     * 总金额，单位：分 必填
     */
    private int payTotalFee;

    /**
     * 应付金额，单位：分 必填
     */
    private int payBehooveFee;

    /**
     * 个人自付金额，单位：分 必填
     */
    private int payActualFee;

    /**
     * 医疗统筹支付金额，单位：分 必填
     */
    private int payMiFee;

    /**
     * 交易响应代码(银行、第三方支付等返回的结果码) 必填
     */
    private String payResCode;

    /**
     * 交易响应描述
     */
    private String payResDesc;

    /**
     * 商户号，对应支付渠道的商户号
     */
    private String merchantId;

    /**
     * 终端号，对应支付渠道的终端号
     */
    private String terminalId;

    /**
     * 银行卡号
     */
    private String bankNo;

    /**
     * 第三方支付时，用户的支付帐号
     */
    private String payAccount;

    /**
     * 操作员ID
     */
    private String operatorId;

    /**
     * 收据号
     */
    private String receiptId;

    List<Map<String, Object>> list = new ArrayList<>();

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

    public String getHospSequence() {
        return hospSequence;
    }

    public void setHospSequence(String hospSequence) {
        this.hospSequence = hospSequence;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public PayChannel getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(PayChannel payChannelId) {
        this.payChannelId = payChannelId;
    }

    public int getPayTotalFee() {
        return payTotalFee;
    }

    public void setPayTotalFee(int payTotalFee) {
        this.payTotalFee = payTotalFee;
    }

    public int getPayBehooveFee() {
        return payBehooveFee;
    }

    public void setPayBehooveFee(int payBehooveFee) {
        this.payBehooveFee = payBehooveFee;
    }

    public int getPayActualFee() {
        return payActualFee;
    }

    public void setPayActualFee(int payActualFee) {
        this.payActualFee = payActualFee;
    }

    public int getPayMiFee() {
        return payMiFee;
    }

    public void setPayMiFee(int payMiFee) {
        this.payMiFee = payMiFee;
    }

    public String getPayResCode() {
        return payResCode;
    }

    public void setPayResCode(String payResCode) {
        this.payResCode = payResCode;
    }

    public String getPayResDesc() {
        return payResDesc;
    }

    public void setPayResDesc(String payResDesc) {
        this.payResDesc = payResDesc;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public String getHospClinicCode() {
        return hospClinicCode;
    }

    public void setHospClinicCode(String hospClinicCode) {
        this.hospClinicCode = hospClinicCode;
    }
}
