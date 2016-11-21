package com.proper.enterprise.isj.pay.weixin.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by think on 2016/10/5 0005.
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class WeixinPayQueryRes {

    /**
     * 返回状态码 必填
     */
    @XmlElement(name = "return_code")
    private String returnCode;

    /**
     * 返回信息
     */
    @XmlElement(name = "return_msg")
    private String returnMsg;

    /**
     * 以下字段在return_code为SUCCESS的时候有返回
     */

    /**
     * 业务结果 必填
     */
    @XmlElement(name = "result_code")
    private String resultCode;

    /**
     * 错误代码
     */
    @XmlElement(name = "err_code")
    private String errCode;

    /**
     * 错误代码描述
     */
    @XmlElement(name = "err_code_des")
    private String errCodeDes;

    /**
     * 应用APPID 必填
     */
    @XmlElement(name = "appid")
    private String appid;

    /**
     * 商户号 必填
     */
    @XmlElement(name = "mch_id")
    private String mchId;

    /**
     * 随机字符串 必填
     */
    @XmlElement(name = "nonce_str")
    private String nonceStr;

    /**
     * 签名 必填
     */
    @XmlElement(name = "sign")
    private String sign;

    /**
     * 设备号
     */
    @XmlElement(name = "device_info")
    private String deviceInfo;

    /**
     * 用户标识
     */
    @XmlElement(name = "openid")
    private String openid;

    /**
     * 交易类型
     */
    @XmlElement(name = "trade_type")
    private String tradeType;

    /**
     * 交易状态 SUCCESS—支付成功 REFUND—转入退款 NOTPAY—未支付 CLOSED—已关闭 REVOKED—已撤销（刷卡支付）
     * USERPAYING--用户支付中 PAYERROR--支付失败(其他原因，如银行返回失败)
     */
    @XmlElement(name = "trade_state")
    private String tradeState;

    /**
     * 总金额
     */
    @XmlElement(name = "total_fee")
    private String totalFee;

    /**
     * 微信支付订单号
     */
    @XmlElement(name = "transaction_id")
    private String transactionId;


    /**
     * 商户订单号
     */
    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 支付完成时间
     */
    @XmlElement(name = "time_end")
    private String timeEnd;


    /**
     *交易状态描述
     */
    @XmlElement(name = "trade_state_desc")
    private String tradeStateDesc;

    /**
     *现金支付金额
     */
    @XmlElement(name = "cash_fee")
    private String cashFee;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrCodeDes() {
        return errCodeDes;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeState() {
        return tradeState;
    }

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTradeStateDesc() {
        return tradeStateDesc;
    }

    public void setTradeStateDesc(String tradeStateDesc) {
        this.tradeStateDesc = tradeStateDesc;
    }

    public String getCashFee() {
        return cashFee;
    }

    public void setCashFee(String cashFee) {
        this.cashFee = cashFee;
    }
}
