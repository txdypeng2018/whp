package com.proper.enterprise.isj.pay.weixin.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信移动支付接收预支付ID信息_XML_Model
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnifiedOrderRes {

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
     * 应用APPID 必填
     */
    @XmlElement(name = "appid")
    private String appId;

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
     * 以下字段在return_code 和result_code都为SUCCESS的时候有返回
     */

    /**
     * 交易类型 必填
     */
    @XmlElement(name = "trade_type")
    private String tradeType;

    /**
     * 预支付交易会话标识 必填
     */
    @XmlElement(name = "prepay_id")
    private String prepayId;

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

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }
}
