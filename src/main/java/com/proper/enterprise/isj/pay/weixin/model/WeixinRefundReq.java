package com.proper.enterprise.isj.pay.weixin.model;

import com.proper.enterprise.isj.pay.weixin.constants.WeixinConstants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信移动支付返回异步通知处理结果_XML_Model
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class WeixinRefundReq {
    /**
     * 应用ID 必填
     */
    @XmlElement(name = "appid")
    private String appid = WeixinConstants.WEIXIN_PAY_APPID;

    /**
     * 商户号 必填
     */
    @XmlElement(name = "mch_id")
    private String mchId = WeixinConstants.WEIXIN_PAY_MCH_ID;

    /**
     * 设备号
     */
    @XmlElement(name = "device_info")
    private String deviceInfo = "WEB";

    /**
     * 随机字符串 必填
     */
    @XmlElement(name = "nonce_str")
    private String nonceStr;

    /**
     * 签名
     */
    @XmlElement(name = "sign")
    private String sign;

    /**
     * 微信订单号 (与商户订单号二选一)
     */
    @XmlElement(name = "transaction_id")
    private String transactionId;

    /**
     * 商户订单号 (与商户订单号二选一)
     */
    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 商户退款单号 必填
     */
    @XmlElement(name = "out_refund_no")
    private String outRefundNo;

    /**
     * 总金额 必填
     */
    @XmlElement(name = "total_fee")
    private int totalFee;

    /**
     * 退款金额 必填
     */
    @XmlElement(name = "refund_fee")
    private int refundFee;

    /**
     * 货币类型
     */
    @XmlElement(name = "refund_fee_type")
    private String refundFeeType = "CNY";

    /**
     * 操作员 必填
     */
    @XmlElement(name = "op_user_id")
    private String opUserId = WeixinConstants.WEIXIN_PAY_MCH_ID;

    /**
     * 退款资金来源
     *
     * REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款/基本账户
     * REFUND_SOURCE_UNSETTLED_FUNDS---未结算资金退款
     */
//    @XmlElement(name = "refund_account")
//    private String refundAccount = WeixinConstants.REFUND_SOURCE_RECHARGE_FUNDS;

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

    public String getRefundFeeType() {
        return refundFeeType;
    }

    public void setRefundFeeType(String refundFeeType) {
        this.refundFeeType = refundFeeType;
    }

//    public String getRefundAccount() {
//        return refundAccount;
//    }
//
//    public void setRefundAccount(String refundAccount) {
//        this.refundAccount = refundAccount;
//    }

    public String getOpUserId() {
        return opUserId;
    }

    public void setOpUserId(String opUserId) {
        this.opUserId = opUserId;
    }
}
