package com.proper.enterprise.isj.webservices.model.req;

/**
 * Created by think on 2016/9/8 0008. 挂号成功后调用。 医院his根据平台订单号（挂号接口有传入）进行挂号订单的支付操作。
 * 用户在支付时，平台将支付结果同步到平台（支付仅限30分钟内提交的订单）。
 * 
 */
public class PayRegReq {

    /**
     * 医院ID 必填
     */
    private String hosId;
    /**
     * 平台订单号 必填
     */
    private String orderId;
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
     * 支付渠道ID，详见 “支付渠道” 必填
     */
    private String payChannelId;
    /**
     * 总金额，单位：分 必填
     */
    private int payTotalFee;
    /**
     * 应付金额，单位：分 必填
     */
    private int payCopeFee;
    /**
     * 实付金额，单位：分 必填
     */
    private int payFee;
    /**
     * 交易响应代码(银行、第三方支付等返回的结果码)
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

    public String getPayChannelId() {
        return payChannelId;
    }

    public void setPayChannelId(String payChannelId) {
        this.payChannelId = payChannelId;
    }

    public int getPayTotalFee() {
        return payTotalFee;
    }

    public void setPayTotalFee(int payTotalFee) {
        this.payTotalFee = payTotalFee;
    }

    public int getPayCopeFee() {
        return payCopeFee;
    }

    public void setPayCopeFee(int payCopeFee) {
        this.payCopeFee = payCopeFee;
    }

    public int getPayFee() {
        return payFee;
    }

    public void setPayFee(int payFee) {
        this.payFee = payFee;
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
}
