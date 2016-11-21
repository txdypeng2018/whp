package com.proper.enterprise.isj.pay.ali.model;

import com.proper.enterprise.isj.pay.ali.constants.AliConstants;

/**
 * 支付宝接收前台请求支付信息参数
 */
public class UnifiedOrderReq {

    /**
     * 签约合作者身份ID
     */
    private String partner = AliConstants.ALI_PAY_PARTNER_ID;
    /**
     * 签约卖家支付宝账号
     */
    private String sellerId = AliConstants.ALI_PAY_SELLER_ID;
    /**
     * 商户网站唯一订单号
     */
    private String outTradeNo;

    /**
     * 商品名称
     */
    private String subject;

    /**
     * 商品详情
     */
    private String body;

    /**
     * 商品总金额
     */
    private String totalFee;
    /**
     * 服务器异步通知页面路径
     */
    private String notifyUrl = AliConstants.ALI_PAY_NOTICE_URL;
    /**
     * 服务接口名称、固定值
     */
    private String service = AliConstants.ALI_PAY_MOBILE_SECURITYPAY_PAY;
    /**
     * 支付类型， 固定值
     */
    private String paymentType = AliConstants.ALI_PAY_PAYMENT_TYPE;
    /**
     * 参数编码， 固定值
     */
    private String inputCharset = AliConstants.ALI_PAY_INPUT_CHARSET;
    /**
     * 设置未付款交易的超时时间
     */
    private String itBPay = AliConstants.ALI_PAY_IT_B_PAY;

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getItBPay() {
        return itBPay;
    }

    public void setItBPay(String itBPay) {
        this.itBPay = itBPay;
    }
}
