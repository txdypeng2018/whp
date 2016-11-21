package com.proper.enterprise.isj.pay.weixin.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信移动支付接收异步通知信息_XML_Model
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnifiedNoticeRes {
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
     * 应用ID 必填
     */
    @XmlElement(name = "appid")
    private String appid;

    /**
     * 商户号 必填
     */
    @XmlElement(name = "mch_id")
    private String mchId;

    /**
     * 设备号
     */
    @XmlElement(name = "device_info")
    private String deviceInfo;

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
     * 用户标识 必填
     */
    @XmlElement(name = "openid")
    private String openid;

    /**
     * 是否关注公众账号
     */
    @XmlElement(name = "is_subscribe")
    private String isSubscribe;

    /**
     * 交易类型 必填
     */
    @XmlElement(name = "trade_type")
    private String tradeType;

    /**
     * 付款银行 必填
     */
    @XmlElement(name = "bank_type")
    private String bankType;

    /**
     * 总金额 必填
     */
    @XmlElement(name = "total_fee")
    private String totalFee;

    /**
     * 货币种类
     */
    @XmlElement(name = "fee_type")
    private String feeType;

    /**
     * 现金支付金额 必填
     */
    @XmlElement(name = "cash_fee")
    private String cashFee;

    /**
     * 现金支付货币类型
     */
    @XmlElement(name = "cash_fee_type")
    private String cashFeeType;

    /**
     * 代金券或立减优惠金额
     */
    @XmlElement(name = "coupon_fee")
    private String couponFee;

    /**
     * 代金券或立减优惠使用数量
     */
    @XmlElement(name = "coupon_count")
    private String couponCount;

    /**
     * 微信支付订单号 必填
     */
    @XmlElement(name = "transaction_id")
    private String transactionId;

    /**
     * 商户内部订单号 唯一 必填
     */
    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 商家数据包
     */
    @XmlElement(name = "attach")
    private String attach;

    /**
     * 支付完成时间 必填
     */
    @XmlElement(name = "time_end")
    private String timeEnd;

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

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getCashFee() {
        return cashFee;
    }

    public void setCashFee(String cashFee) {
        this.cashFee = cashFee;
    }

    public String getCashFeeType() {
        return cashFeeType;
    }

    public void setCashFeeType(String cashFeeType) {
        this.cashFeeType = cashFeeType;
    }

    public String getCouponFee() {
        return couponFee;
    }

    public void setCouponFee(String couponFee) {
        this.couponFee = couponFee;
    }

    public String getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(String couponCount) {
        this.couponCount = couponCount;
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

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }
}
