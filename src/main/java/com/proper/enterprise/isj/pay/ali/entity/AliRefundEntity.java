package com.proper.enterprise.isj.pay.ali.entity;

import com.proper.enterprise.isj.pay.ali.model.AliRefund;
import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 支付宝退款Entity
 */
@Entity
@Table(name = "ISJ_ALI_REFUNDINFO")
@CacheEntity
public class AliRefundEntity extends BaseEntity implements AliRefund {

    public AliRefundEntity() { }

    public AliRefundEntity(String sign, String tradeNo, String totalFee, String refundResult, String notifyTime, String signType, String notifyType, String notifyId, String batchNo, String successNum) {
        this.sign = sign;
        this.tradeNo = tradeNo;
        this.totalFee = totalFee;
        this.refundResult = refundResult;
        this.notifyTime = notifyTime;
        this.signType = signType;
        this.notifyType = notifyType;
        this.notifyId = notifyId;
        this.batchNo = batchNo;
        this.successNum = successNum;
    }

    /**
     * 签名 必填
     */
    private String sign;

    /**
     * 支付宝交易订单号
     */
    private String tradeNo;

    /**
     * 退款金额
     */
    private String totalFee;

    /**
     * 退款结果
     */
    private String refundResult;

    /**
     * 异步通知时间
     */
    private String notifyTime;

    /**
     * 签名方式
     */
    private String signType;

    /**
     * 异步通知方式
     */
    private String notifyType;

    /**
     * 异步通知ID
     */
    private String notifyId;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 退款成功总数
     */
    private String successNum;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getRefundResult() {
        return refundResult;
    }

    public void setRefundResult(String refundResult) {
        this.refundResult = refundResult;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(String successNum) {
        this.successNum = successNum;
    }
}
