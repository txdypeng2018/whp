package com.proper.enterprise.isj.pay.ali.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.proper.enterprise.isj.pay.ali.model.AliRefund;
import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

/**
 * 支付宝退款Entity
 */
@Entity
@Table(name = "ISJ_ALI_REFUNDINFO")
@CacheEntity
public class AliRefundEntity extends BaseEntity implements AliRefund {

    public AliRefundEntity() {
    }

    private String code;

    private String msg;

    /**
     * 支付宝交易号 必填
     */
    private String tradeNo;

    /**
     * 商户订单号 必填
     */
    private String outTradeNo;

    /**
     * 买家支付宝用户号，该参数已废弃，请不要使用 必填
     */
    private String openId;

    /**
     * 用户的登录id 必填
     */
    private String buyerLogonId;

    /**
     * 本次退款是否发生了资金变化 必填
     */
    private String fundChange;

    /**
     * 退款总金额 必填
     */
    private String refundFee;

    /**
     * 退款支付时间 必填
     */
    private String gmtRefundPay;

    /**
     * 交易在支付时候的门店名称
     */
    private String storeName;

    /**
     * 买家在支付宝的用户id 必填
     */
    private String buyerUserId;

    /**
     * 本次商户实际退回金额
     */
    private String sendBackFee;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
    public String getOutTradeNo() {
        return outTradeNo;
    }

    @Override
    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Override
    public String getOpenId() {
        return openId;
    }

    @Override
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String getBuyerLogonId() {
        return buyerLogonId;
    }

    @Override
    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }

    @Override
    public String getFundChange() {
        return fundChange;
    }

    @Override
    public void setFundChange(String fundChange) {
        this.fundChange = fundChange;
    }

    @Override
    public String getRefundFee() {
        return refundFee;
    }

    @Override
    public void setRefundFee(String refundFee) {
        this.refundFee = refundFee;
    }

    @Override
    public String getGmtRefundPay() {
        return gmtRefundPay;
    }

    @Override
    public void setGmtRefundPay(String gmtRefundPay) {
        this.gmtRefundPay = gmtRefundPay;
    }

    @Override
    public String getStoreName() {
        return storeName;
    }

    @Override
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String getBuyerUserId() {
        return buyerUserId;
    }

    @Override
    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    @Override
    public String getSendBackFee() {
        return sendBackFee;
    }

    @Override
    public void setSendBackFee(String sendBackFee) {
        this.sendBackFee = sendBackFee;
    }
    
}
