package com.proper.enterprise.isj.order.model;

/**
 * 订单状态确认
 */
public class OrderConfirmReq {
    /**
     * 订单号
     */
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
