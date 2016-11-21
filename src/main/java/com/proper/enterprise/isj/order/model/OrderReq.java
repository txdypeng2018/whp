package com.proper.enterprise.isj.order.model;

/**
 * 订单请求
 */
public class OrderReq {
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private String paymentStatus;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
