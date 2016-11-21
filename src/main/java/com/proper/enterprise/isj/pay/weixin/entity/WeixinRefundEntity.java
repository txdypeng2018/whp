package com.proper.enterprise.isj.pay.weixin.entity;

import com.proper.enterprise.isj.pay.weixin.model.WeixinRefund;
import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ISJ_WEIXIN_REFUNDINFO")
@CacheEntity
public class WeixinRefundEntity  extends BaseEntity implements WeixinRefund {
    /**
     * 返回状态码 必填
     */
    private String returnCode;

    /**
     * 返回信息
     */
    private String returnMsg;

    /**
     * 以下字段在return_code为SUCCESS的时候有返回
     */

    /**
     * 业务结果 必填
     */
    private String resultCode;

    /**
     * 错误代码
     */
    private String errCode;

    /**
     * 错误代码描述
     */
    private String errCodeDes;

    /**
     * 应用APPID 必填
     */
    private String appId;

    /**
     * 商户号 必填
     */
    private String mchId;

    /**
     * 设备号
     */
    private String deviceInfo;

    /**
     * 随机字符串 必填
     */
    private String nonceStr;

    /**
     * 签名 必填
     */
    private String sign;

    /**
     * 微信订单号 必填
     */
    private String transactionId;

    /**
     * 商户订单号 必填
     */
    private String outTradeNo;

    /**
     * 商户退款单号 必填
     */
    private String outRefundNo;

    /**
     * 微信退款单号 必填
     */
    private String refundId;

    /**
     * 退款渠道
     */
    private String refundChannel;

    /**
     * 退款金额 必填
     */
    private String refundFee;

    /**
     * 订单总金额 必填
     */
    private String totalFee;

    /**
     * 订单金额货币种类
     */
    private String feeType;

    /**
     * 现金支付金额 必填
     */
    private String cashFee;

    /**
     * 现金退款金额
     */
    private String cashRefundFee;

    /**
     * 代金券或立减优惠退款金额
     */
    private String couponRefundFee;
    /**
     * 代金券或立减优惠使用数量
     */
    private String couponRefundCount;

    /**
     * 代金券或立减优惠ID
     */
    private String couponRefundId;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public String getOutRefundNo() {
        return outRefundNo;
    }

    public void setOutRefundNo(String outRefundNo) {
        this.outRefundNo = outRefundNo;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getRefundChannel() {
        return refundChannel;
    }

    public void setRefundChannel(String refundChannel) {
        this.refundChannel = refundChannel;
    }

    public String getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(String refundFee) {
        this.refundFee = refundFee;
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

    public String getCashRefundFee() {
        return cashRefundFee;
    }

    public void setCashRefundFee(String cashRefundFee) {
        this.cashRefundFee = cashRefundFee;
    }

    public String getCouponRefundFee() {
        return couponRefundFee;
    }

    public void setCouponRefundFee(String couponRefundFee) {
        this.couponRefundFee = couponRefundFee;
    }

    public String getCouponRefundCount() {
        return couponRefundCount;
    }

    public void setCouponRefundCount(String couponRefundCount) {
        this.couponRefundCount = couponRefundCount;
    }

    public String getCouponRefundId() {
        return couponRefundId;
    }

    public void setCouponRefundId(String couponRefundId) {
        this.couponRefundId = couponRefundId;
    }
}