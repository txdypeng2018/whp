package com.proper.enterprise.isj.payment.logger;

/**
 * 支付日志相关常量.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public final class PayLogConstrants {

    /**
     * 阶段部分掩码.
     *
     * @since 0.1.0
     */
    public static final int STEP_FIELD_MASK = 0xFFF0;

    /**
     * 每个阶段的状态部分的掩码.
     *
     * @since 0.1.0
     */
    public static final int STATUS_FIELD_MASK = 0x000F;

    /**
     * 默认状态.
     *
     * @since 0.1.0
     */
    public static final int STATUS_DEFAULT = 0x0000;

    /**
     * 开始执行状态的掩码.
     *
     * @since 0.1.0
     */
    public static final int STATUS_MASK_START = 0x0001;

    /**
     * 执行成功状态的掩码.
     *
     * @since 0.1.0
     */
    public static final int STATUS_MASK_SUCCESS = 0x0002;

    /**
     * 执行发生异常的掩码.
     *
     * @since 0.1.0
     */
    public static final int STATUS_MASK_EXCEPTION = 0x0004;
    /**
     * 执行失败的掩码.
     *
     * @since 0.1.0
     */
    public static final int STATUS_MASK_FAIL = 0x0008;

    /**
     * 尚未开始支付阶段的掩码.
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_UNPAY = 0x0000;

    /**
     * 预支付阶段的掩码.
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_PREPAY = 0x0010;

    /**
     * 确认订单阶段的掩码.
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_ORDER = 0x0020;

    /**
     * 确认支付阶段的掩码.
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_PAY = 0x0040;

    /**
     * 挂号阶段的掩码.
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_REG = 0x0080;

    /**
     * 诊见缴费.
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_RECIPE_PAY = 0x0100;

    /**
     * 退款阶段的掩码.
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_REFUND = 0x0800;

    /**
     * 重试阶段的掩码.
     * <p>
     * 需要延时退款.
     * </p>
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_UNKNOWN_AND_RETRY = 0x1000;

    /**
     * 重试发现已经成功状态的掩码.
     *
     * @since 0.1.0
     */
    public static final int STEP_MASK_KNOWN_SUCCESS = 0x2000;

}
