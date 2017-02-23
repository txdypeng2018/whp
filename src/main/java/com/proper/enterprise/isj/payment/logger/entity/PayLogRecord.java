package com.proper.enterprise.isj.payment.logger.entity;

/**
 * 支付日志记录.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface PayLogRecord {

    String getOrderId();

    void setOrderId(String orderId);

    /**
     * 总支付金额.
     *
     * @return 总支付金额.
     * @since 0.1.0
     */
    String getTotalFee();

    /**
     * 总支付金额.
     *
     * @param totalFee 总支付金额.
     * @since 0.1.0
     */
    void setTotalFee(String totalFee);

    /**
     * 支付用户Id.
     *
     * @return 支付用户Id.
     * @since 0.1.0
     */
    String getUserId();

    /**
     * 支付用户Id.
     *
     * @param userId 支付用户Id.
     * @since 0.1.0
     */
    void setUserId(String userId);

    /**
     * 支付渠道.
     *
     * @return 支付渠道.
     * @since 0.1.0
     */
    String getPayWay();

    /**
     * 支付渠道.
     *
     * @param payWay 支付渠道.
     * @since 0.1.0
     */
    void setPayWay(String payWay);


    /**
     * 退款总额.
     *
     * @return 退款总额.
     * @since 0.1.0
     */
    String getRefundAmount();

    /**
     * 退款总额.
     *
     * @param refundAmount 退款总额.
     * @since 0.1.0
     */
    void setRefundAmount(String refundAmount);

    /**
     * 退款渠道.
     *
     * @return 退款渠道.
     * @since 0.1.0
     */
    String getRefundWay();

    /**
     * 退款渠道.
     *
     * @param refundWay 退款渠道.
     * @since 0.1.0
     */
    void setRefundWay(String refundWay);

}
