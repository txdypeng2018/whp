package com.proper.enterprise.isj.pay.ali.model;

/**
 * 支付宝订单确认
 */
public class AliConfirmReq {
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
