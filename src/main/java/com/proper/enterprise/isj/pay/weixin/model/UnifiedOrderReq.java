package com.proper.enterprise.isj.pay.weixin.model;

import com.proper.enterprise.isj.pay.weixin.constants.WeixinConstants;
import com.proper.enterprise.isj.pay.weixin.adapter.SignAdapter;
import com.proper.enterprise.isj.pay.weixin.adapter.TimestampAdapter;
import com.proper.enterprise.platform.core.utils.ConfCenter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * 微信移动支付请求预支付ID_XML_Model
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnifiedOrderReq {

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
    @XmlJavaTypeAdapter(SignAdapter.class)
    private UnifiedOrderReq sign = this;

    /**
     * 商品描述 必填
     */
    @XmlElement(name = "body")
    private String body;

    /**
     * 商品详情
     */
    @XmlElement(name = "detail")
    private String detail;

    /**
     * 附加数据
     */
    @XmlElement(name = "attach")
    private String attach;

    /**
     * 商户订单号 必填
     */
    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 货币类型
     */
    @XmlElement(name = "fee_type")
    private String feeType = "CNY";

    /**
     * 总金额 必填
     */
    @XmlElement(name = "total_fee")
    private int totalFee;

    /**
     * 终端IP 必填
     */
    @XmlElement(name = "spbill_create_ip")
    private String spbillCreateIp;

    /**
     * 交易起始时间
     */
    @XmlElement(name = "time_start")
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    private Date timeStart;

    /**
     * 交易结束时间
     */
    @XmlElement(name = "time_expire")
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    private Date timeExpire;

    /**
     * 商品标记
     */
    @XmlElement(name = "goods_tag")
    private String goodsTag;

    /**
     * 通知地址 必填
     */
    @XmlElement(name = "notify_url")
    private String notifyUrl = ConfCenter.get("isj.pay.wx.url.notify");

    /**
     * 交易类型 必填
     */
    @XmlElement(name = "trade_type")
    private String tradeType = "APP";

    /**
     * 指定支付方式
     */
    @XmlElement(name = "limit_pay")
    private String limitPay;

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public String getGoodsTag() {
        return goodsTag;
    }

    public void setGoodsTag(String goodsTag) {
        this.goodsTag = goodsTag;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getLimitPay() {
        return limitPay;
    }

    public void setLimitPay(String limitPay) {
        this.limitPay = limitPay;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    public Date getTimeStart() {
        return timeStart == null ? null : (Date) timeStart.clone();
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = (Date) timeStart.clone();
    }

    public Date getTimeExpire() {
        return timeExpire == null ? null : (Date) timeExpire.clone();
    }

    public void setTimeExpire(Date timeExpire) {
        this.timeExpire = (Date) timeExpire.clone();
    }

    public UnifiedOrderReq getSign() {
        return sign;
    }

    public void setSign(UnifiedOrderReq sign) {
        this.sign = sign;
    }
}
