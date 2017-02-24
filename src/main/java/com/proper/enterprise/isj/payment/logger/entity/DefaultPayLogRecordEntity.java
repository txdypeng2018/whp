package com.proper.enterprise.isj.payment.logger.entity;

import com.proper.enterprise.platform.core.entity.BaseEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * 支付日志信息.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
@Entity
@Table(name = "ISJ_PAY_LOG")
@Component
@Scope("prototype")
public class DefaultPayLogRecordEntity extends BaseEntity implements PayLogRecord, LogContent, PayStep {

    /**
     * 序列化版本编号.
     */
    private static final long serialVersionUID = -4260678113508073118L;

    @Column(length = 63)
    private String orderId;

    /**
     * 商品总金额（以分为单位）
     */
    private String totalFee;

    /**
     * 用户ID
     */
    @Column(length = 63)
    private String userId;

    /**
     * 支付方式
     */
    @Column(length = 20)
    private String payWay;

    /**
     * 退款金额(以分为单位)
     */
    private String refundAmount;

    /**
     * 退款方式
     */
    private String refundWay;

    /**
     * java对象类型.
     */
    private String javaType;

    private int cause;

    /**
     * 日志内容json字符串.
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "text", nullable = true)
    private String content;
    @Column(length = 25)
    private String logCtxBirthTm;
    @Column(length = 25)
    private String logTm;
    @Column(length = 25)
    private String writeTm;
    @Column(length = 100)
    private String thread;

    private int stepStatus;

    private int step;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundWay() {
        return refundWay;
    }

    public void setRefundWay(String refundWay) {
        this.refundWay = refundWay;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLogCtxBirthTm() {
        return logCtxBirthTm;
    }

    public void setLogCtxBirthTm(String logCtxBirthTm) {
        this.logCtxBirthTm = logCtxBirthTm;
    }

    public String getLogTm() {
        return logTm;
    }

    public void setLogTm(String logTm) {
        this.logTm = logTm;
    }

    public String getWriteTm() {
        return writeTm;
    }

    public void setWriteTm(String writeTm) {
        this.writeTm = writeTm;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    @Override
    public int getStep() {
        return step;
    }

    @Override
    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public int getStepStatus() {
        return stepStatus;
    }

    @Override
    public void setStepStatus(int status) {
        this.stepStatus = status;
    }

    @Override
    public void setCause(int cause) {
        this.cause = cause;
    }

    @Override
    public int getCause() {
        return this.cause;
    }

}
