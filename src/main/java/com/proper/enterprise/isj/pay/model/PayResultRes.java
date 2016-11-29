package com.proper.enterprise.isj.pay.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 支付统一返回对象
 */
public class PayResultRes {

    /**
     * 结果
     */
    private String resultCode;

    /**
     * 消息
     */
    private String resultMsg;

    /**
     * 支付宝支付信息
     */
    @JsonProperty("pay_info")
    private String payInfo;

    /**
     * 支付宝秘钥
     */
    private String key;

    /**
     * 微信支付应用ID
     */
    private String appid;

    /**
     * 微信支付商户号
     */
    private String partnerid;

    /**
     * 微信支付预支付交易会话ID
     */
    private String prepayid;

    /**
     * 微信支付扩展字段
     */
    @JsonProperty("package")
    private String wxpackage;

    /**
     * 微信支付随机字符串
     */
    private String noncestr;

    /**
     * 微信支付时间戳
     */
    private String timestamp;

    /**
     * 签名
     */
    private String sign;

    /**
     * 一网通订单号
     */
    private String cmbBillNo;

    /**
     * 一网通订单日期
     */
    private String cmbDate;

    /**
     * 一网通订单金额
     */
    private String amout;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getWxpackage() {
        return wxpackage;
    }

    public void setWxpackage(String wxpackage) {
        this.wxpackage = wxpackage;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getCmbBillNo() {
        return cmbBillNo;
    }

    public void setCmbBillNo(String cmbBillNo) {
        this.cmbBillNo = cmbBillNo;
    }

    public String getCmbDate() {
        return cmbDate;
    }

    public void setCmbDate(String cmbDate) {
        this.cmbDate = cmbDate;
    }

    public String getAmout() {
        return amout;
    }

    public void setAmout(String amout) {
        this.amout = amout;
    }
}
