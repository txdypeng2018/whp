package com.proper.enterprise.isj.pay.ali.entity;

import com.proper.enterprise.isj.pay.ali.model.Ali;
import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 支付宝支付Entity
 */
@Entity
@Table(name = "ISJ_ALI_PAYINFO")
@CacheEntity
public class AliEntity extends BaseEntity implements Ali {

    public AliEntity() { }

    public AliEntity(String notifyTime, String notifyType, String notifyId, String signType, String sign, String outTradeNo, String subject, String paymentType, String tradeNo, String tradeStatus, String sellerId, String sellerEmail, String buyerId, String buyerEmail, String totalFee, String quantity, String price, String body, String gmtCreate, String gmtPayment, String isTotalFeeAdjust, String useCoupon, String discount, String refundStatus, String gmtRefund, String isdel, String gmtClose) {
        this.notifyTime = notifyTime;
        this.notifyType = notifyType;
        this.notifyId = notifyId;
        this.signType = signType;
        this.sign = sign;
        this.outTradeNo = outTradeNo;
        this.subject = subject;
        this.paymentType = paymentType;
        this.tradeNo = tradeNo;
        this.tradeStatus = tradeStatus;
        this.sellerId = sellerId;
        this.sellerEmail = sellerEmail;
        this.buyerId = buyerId;
        this.buyerEmail = buyerEmail;
        this.totalFee = totalFee;
        this.quantity = quantity;
        this.price = price;
        this.body = body;
        this.gmtCreate = gmtCreate;
        this.gmtPayment = gmtPayment;
        this.isTotalFeeAdjust = isTotalFeeAdjust;
        this.useCoupon = useCoupon;
        this.discount = discount;
        this.refundStatus = refundStatus;
        this.gmtRefund = gmtRefund;
        this.isdel = isdel;
        this.gmtClose = gmtClose;
    }

    /**
     * 通知时间 不可空
     */
    @Column(nullable = false)
    private String notifyTime;

    /**
     * 通知类型 不可空
     */
    @Column(nullable = false)
    private String notifyType;

    /**
     * 通知校验ID 不可空
     */
    @Column(nullable = false)
    private String notifyId;

    /**
     * 签名方式 不可空
     */
    @Column(nullable = false)
    private String signType;

    /**
     * 签名 不可空
     */
    @Column(nullable = false)
    private String sign;

    /**
     * 商户网站唯一订单号 可空
     */
    private String outTradeNo;

    /**
     * 商品名称 可空
     */
    private String subject;

    /**
     * 支付类型 可空
     */
    private String paymentType;

    /**
     * 支付宝交易号 不可空
     */
    @Column(nullable = false)
    private String tradeNo;

    /**
     * 交易状态 不可空
     */
    @Column(nullable = false)
    private String tradeStatus;

    /**
     * 卖家支付宝用户号 不可空
     */
    @Column(nullable = false)
    private String sellerId;

    /**
     * 卖家支付宝账号 不可空
     */
    @Column(nullable = false)
    private String sellerEmail;

    /**
     * 买家支付宝用户号 不可空
     */
    @Column(nullable = false)
    private String buyerId;

    /**
     * 买家支付宝账号 不可空
     */
    @Column(nullable = false)
    private String buyerEmail;

    /**
     * 交易金额 不可空
     */
    @Column(nullable = false)
    private String totalFee;

    /**
     * 购买数量 可空
     */
    private String quantity;

    /**
     * 商品单价 可空
     */
    private String price;

    /**
     * 商品描述 可空
     */
    private String body;

    /**
     * 交易创建时间 可空
     */
    private String gmtCreate;

    /**
     * 交易付款时间 可空
     */
    private String gmtPayment;

    /**
     * 是否调整总价 可空
     */
    private String isTotalFeeAdjust;

    /**
     * 是否使用红包买家 可空
     */
    private String useCoupon;

    /**
     * 折扣 可空
     */
    private String discount;

    /**
     * 退款状态 可空
     */
    private String refundStatus;

    /**
     * 退款时间 可空
     */
    private String gmtRefund;

    /**
     * 逻辑删除 0:正常/1:逻辑删除
     */
    @Column(nullable = false)
    private String isdel;

    /**
     * 交易关闭时间 可空
     */
    private String gmtClose;

    @Override
    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    @Override
    public String getNotifyType() {
        return notifyType;
    }

    @Override
    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    @Override
    public String getNotifyId() {
        return notifyId;
    }

    @Override
    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    @Override
    public String getSignType() {
        return signType;
    }

    @Override
    public void setSignType(String signType) {
        this.signType = signType;
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
    public String getOutTradeNo() {
        return outTradeNo;
    }

    @Override
    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getPaymentType() {
        return paymentType;
    }

    @Override
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String getTradeNo() {
        return tradeNo;
    }

    @Override
    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Override
    public String getTradeStatus() {
        return tradeStatus;
    }

    @Override
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Override
    public String getSellerId() {
        return sellerId;
    }

    @Override
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public String getSellerEmail() {
        return sellerEmail;
    }

    @Override
    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    @Override
    public String getBuyerId() {
        return buyerId;
    }

    @Override
    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    @Override
    public String getBuyerEmail() {
        return buyerEmail;
    }

    @Override
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
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
    public String getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String getGmtPayment() {
        return gmtPayment;
    }

    public void setGmtPayment(String gmtPayment) {
        this.gmtPayment = gmtPayment;
    }

    @Override
    public String getIsTotalFeeAdjust() {
        return isTotalFeeAdjust;
    }

    @Override
    public void setIsTotalFeeAdjust(String isTotalFeeAdjust) {
        this.isTotalFeeAdjust = isTotalFeeAdjust;
    }

    @Override
    public String getUseCoupon() {
        return useCoupon;
    }

    @Override
    public void setUseCoupon(String useCoupon) {
        this.useCoupon = useCoupon;
    }

    @Override
    public String getDiscount() {
        return discount;
    }

    @Override
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @Override
    public String getRefundStatus() {
        return refundStatus;
    }

    @Override
    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    @Override
    public String getGmtRefund() {
        return gmtRefund;
    }

    public void setGmtRefund(String gmtRefund) {
        this.gmtRefund = gmtRefund;
    }

    @Override
    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }

    public String getGmtClose() {
        return gmtClose;
    }

    public void setGmtClose(String gmtClose) {
        this.gmtClose = gmtClose;
    }
}