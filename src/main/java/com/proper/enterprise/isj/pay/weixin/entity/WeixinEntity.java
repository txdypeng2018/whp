package com.proper.enterprise.isj.pay.weixin.entity;

import com.proper.enterprise.isj.pay.weixin.model.Weixin;
import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "ISJ_WEIXIN_PAYINFO")
@CacheEntity
public class WeixinEntity extends BaseEntity implements Weixin {

    public WeixinEntity() { }

    public WeixinEntity(String returnCode, String returnMsg, String appid, String mchId, String deviceInfo, String nonceStr, String sign, String resultCode, String errCode, String errCodeDes, String openid, String isSubscribe, String tradeType, String bankType, String totalFee, String feeType, String cashFee, String cashFeeType, String couponFee, String couponCount, String transactionId, String outTradeNo, String attach, String timeEnd, String isdel) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.appid = appid;
        this.mchId = mchId;
        this.deviceInfo = deviceInfo;
        this.nonceStr = nonceStr;
        this.sign = sign;
        this.resultCode = resultCode;
        this.errCode = errCode;
        this.errCodeDes = errCodeDes;
        this.openid = openid;
        this.isSubscribe = isSubscribe;
        this.tradeType = tradeType;
        this.bankType = bankType;
        this.totalFee = totalFee;
        this.feeType = feeType;
        this.cashFee = cashFee;
        this.cashFeeType = cashFeeType;
        this.couponFee = couponFee;
        this.couponCount = couponCount;
        this.transactionId = transactionId;
        this.outTradeNo = outTradeNo;
        this.attach = attach;
        this.timeEnd = timeEnd;
        this.isdel = isdel;
    }

    /**
     * 返回状态码 必填
     */
    @Column(nullable = false)
    private String returnCode;

    /**
     * 返回信息
     */
    private String returnMsg;

    /**
     * 以下字段在return_code为SUCCESS的时候有返回
     */

    /**
     * 应用ID 必填
     */
    @Column(nullable = false)
    private String appid;

    /**
     * 商户号 必填
     */
    @Column(nullable = false)
    private String mchId;

    /**
     * 设备号
     */
    private String deviceInfo;

    /**
     * 随机字符串 必填
     */
    @Column(nullable = false)
    private String nonceStr;

    /**
     * 签名 必填
     */
    @Column(nullable = false)
    private String sign;

    /**
     * 业务结果 必填
     */
    @Column(nullable = false)
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
     * 用户标识 必填
     */
    @Column(nullable = false)
    private String openid;

    /**
     * 是否关注公众账号
     */
    private String isSubscribe;

    /**
     * 交易类型 必填
     */
    @Column(nullable = false)
    private String tradeType;

    /**
     * 付款银行 必填
     */
    @Column(nullable = false)
    private String bankType;

    /**
     * 总金额 必填
     */
    @Column(nullable = false)
    private String totalFee;

    /**
     * 货币种类
     */
    private String feeType;

    /**
     * 现金支付金额 必填
     */
    @Column(nullable = false)
    private String cashFee;

    /**
     * 现金支付货币类型
     */
    private String cashFeeType;

    /**
     * 代金券或立减优惠金额
     */
    private String couponFee;

    /**
     * 代金券或立减优惠使用数量
     */
    private String couponCount;

    /**
     * 微信支付订单号 必填
     */
    @Column(nullable = false)
    private String transactionId;

    /**
     * 商户内部订单号 唯一 必填
     */
    @Column(unique = true, nullable = false)
    private String outTradeNo;

    /**
     * 商家数据包
     */
    private String attach;

    /**
     * 支付完成时间 必填
     */
    @Column(nullable = false)
    private String timeEnd;

    /**
     * 以下字段为系统内部字段
     */

    /**
     * 逻辑删除 0:正常/1:逻辑删除
     */
    @Column(nullable = false)
    private String isdel;

    @Override
    public String getReturnCode() {
        return returnCode;
    }

    @Override
    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    @Override
    public String getReturnMsg() {
        return returnMsg;
    }

    @Override
    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    @Override
    public String getAppid() {
        return appid;
    }

    @Override
    public void setAppid(String appid) {
        this.appid = appid;
    }

    @Override
    public String getMchId() {
        return mchId;
    }

    @Override
    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    @Override
    public String getDeviceInfo() {
        return deviceInfo;
    }

    @Override
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Override
    public String getNonceStr() {
        return nonceStr;
    }

    @Override
    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    @Override
    public String getSign() {
        return sign;
    }

    @Override
    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String getErrCode() {
        return errCode;
    }

    @Override
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    @Override
    public String getErrCodeDes() {
        return errCodeDes;
    }

    @Override
    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

    @Override
    public String getOpenid() {
        return openid;
    }

    @Override
    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String getIsSubscribe() {
        return isSubscribe;
    }

    @Override
    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    @Override
    public String getTradeType() {
        return tradeType;
    }

    @Override
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    @Override
    public String getBankType() {
        return bankType;
    }

    @Override
    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    @Override
    public String getTotalFee() {
        return totalFee;
    }

    @Override
    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    @Override
    public String getFeeType() {
        return feeType;
    }

    @Override
    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    @Override
    public String getCashFee() {
        return cashFee;
    }

    @Override
    public void setCashFee(String cashFee) {
        this.cashFee = cashFee;
    }

    @Override
    public String getCashFeeType() {
        return cashFeeType;
    }

    @Override
    public void setCashFeeType(String cashFeeType) {
        this.cashFeeType = cashFeeType;
    }

    @Override
    public String getCouponFee() {
        return couponFee;
    }

    @Override
    public void setCouponFee(String couponFee) {
        this.couponFee = couponFee;
    }

    @Override
    public String getCouponCount() {
        return couponCount;
    }

    @Override
    public void setCouponCount(String couponCount) {
        this.couponCount = couponCount;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String getOutTradeNo() {
        return outTradeNo;
    }

    @Override
    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Override
    public String getAttach() {
        return attach;
    }

    @Override
    public void setAttach(String attach) {
        this.attach = attach;
    }

    @Override
    public String getTimeEnd() {
        return timeEnd;
    }

    @Override
    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    @Override
    public String getIsdel() {
        return isdel;
    }

    @Override
    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }
}
