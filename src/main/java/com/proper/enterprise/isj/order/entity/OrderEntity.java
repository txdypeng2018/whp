package com.proper.enterprise.isj.order.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.proper.enterprise.isj.order.model.Order;
import com.proper.enterprise.platform.core.annotation.CacheEntity;
import com.proper.enterprise.platform.core.entity.BaseEntity;

@Entity
@Table(name = "ISJ_ORDERINFO")
@CacheEntity
public class OrderEntity extends BaseEntity implements Order {

    public OrderEntity() {
    }

    public OrderEntity(String orderNo, int paymentStatus, int isdel) {
        this.orderNo = orderNo;
        this.paymentStatus = paymentStatus;
        this.isdel = isdel;
    }

    /**
     * 订单号
     */
    @Column(unique = true, nullable = false)
    private String orderNo;

    /**
     * 支付状态
     *
     * 0 : 未支付 1 : 支付待确认 2 : 支付成功 3:退款 4 : 支付失败
     */
    @Column(nullable = false)
    private int paymentStatus;

    /**
     * 支付方式 2:支付宝,3:微信
     */
    private String payWay;

    /**
     * 逻辑删除
     *
     * 0 : 正常 1 : 逻辑删除
     */
    @Column(nullable = false)
    private int isdel;

    /**
     * 挂号单Id/缴费单Id
     */
    @Column(unique = true, nullable = false)
    private String formId;

    /**
     * 单Id的class实例
     */
    @Column(length = 500)
    private String formClassInstance;

    /**
     * 订单状态 0:订单取消,1:订单待缴费,2:订单缴费成功,3:退费成功,4:缴费失败,5:退费失败
     */
    @Column(nullable = false)
    private String orderStatus = String.valueOf(1);

    /**
     * 取消时间，格式：YYYY-MM-DD HI24:MI:SS
     */
    private String cancelDate;

    /**
     * 取消原因
     */
    private String cancelRemark;

    /**
     * 订单金额
     */
    private String orderAmount;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getIsdel() {
        return isdel;
    }

    public void setIsdel(int isdel) {
        this.isdel = isdel;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }

    public String getCancelRemark() {
        return cancelRemark;
    }

    public void setCancelRemark(String cancelRemark) {
        this.cancelRemark = cancelRemark;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getFormClassInstance() {
        return formClassInstance;
    }

    public void setFormClassInstance(String formClassInstance) {
        this.formClassInstance = formClassInstance;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }
}
