package com.proper.enterprise.isj.pay.cmb.entity;

import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 一网通用户协议异步通知结果Entity
 */
@Entity
@Table(name = "ISJ_CMB_PAY_INFO")
@CacheEntity
public class CmbPayEntity extends BaseEntity {

    /**
     * 用户id
     */
    @Column(nullable = false)
    private String userId;

    /**
     * 支付结果,系统只通知成功交易 必填
     */
    @Column(nullable = false)
    private String succeed;

    /**
     * 支付结果,系统只通知成功交易 必填
     */
    @Column(nullable = false)
    private String coNo;

    /**
     * 定单号(由支付命令送来)； 必填
     */
    @Column(nullable = false)
    private String billNo;

    /**
     * 订单金额(由支付命令送来) 必填
     */
    @Column(nullable = false)
    private String amount;

    /**
     * 交易日期(由支付命令送来) 必填
     */
    @Column(nullable = false)
    private String date;

    /**
     * 商户自定义参数(支付接口中MerchantPara送来) 选填
     */
    private String merchantPara;

    /**
     * 银行通知商户的支付结果消息 必填
     */
    private String msg;

    /**
     * 当前订单是否有优惠，Y:有优惠。 选填
     */
    private String discountFlag;

    /**
     * 优惠金额，格式：xxxx.xx 选填
     */
    private String discountAmt;

    /**
     * 银行用自己的Private Key对通知命令的签名 选填
     */
    private String signature;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSucceed() {
        return succeed;
    }

    public void setSucceed(String succeed) {
        this.succeed = succeed;
    }

    public String getCoNo() {
        return coNo;
    }

    public void setCoNo(String coNo) {
        this.coNo = coNo;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMerchantPara() {
        return merchantPara;
    }

    public void setMerchantPara(String merchantPara) {
        this.merchantPara = merchantPara;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDiscountFlag() {
        return discountFlag;
    }

    public void setDiscountFlag(String discountFlag) {
        this.discountFlag = discountFlag;
    }

    public String getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(String discountAmt) {
        this.discountAmt = discountAmt;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
