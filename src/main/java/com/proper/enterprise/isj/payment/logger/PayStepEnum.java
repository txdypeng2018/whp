package com.proper.enterprise.isj.payment.logger;

import static com.proper.enterprise.isj.payment.logger.PayLogConstrants.*;

/**
 * 支付阶段枚举类.
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @see PayLogConstrants
 * @since 0.1.0
 */
public enum PayStepEnum {
    /**
     * 支付阶段的默认值.
     *
     * @since 0.1.0
     */
    BEGIN(STEP_MASK_UNPAY),
    /**
     * 预支付阶段.
     *
     * @since 0.1.0
     */
    PREPAY(STEP_MASK_PREPAY),
    /**
     * 预支付执行中.
     *
     * @since 0.1.0
     */
    PREPAYING(STEP_MASK_PREPAY | STATUS_MASK_START),
    /**
     * 预支付完成.
     *
     * @since 0.1.0
     */
    PREPAYED(STEP_MASK_PREPAY | STATUS_MASK_START | STATUS_MASK_SUCCESS),
    /**
     * 预支付过程中发生异常，预支付没有正常完成.
     *
     * @since 0.1.0
     */
    PREPAY_EXCEPTION(STEP_MASK_PREPAY | STATUS_MASK_START | STATUS_MASK_EXCEPTION),
    /**
     * 预支付失败.
     *
     * @since 0.1.0
     */
    PREPAY_FAIL(STEP_MASK_PREPAY | STATUS_MASK_START | STATUS_MASK_FAIL),
    /**
     * 下单阶段.
     *
     * @since 0.1.0
     */
    ORDER(STEP_MASK_ORDER),
    /**
     * 正在确认订单.
     *
     * @since 0.1.0
     */
    ORDERING(STEP_MASK_ORDER | STATUS_MASK_START),
    /**
     * 订单已确认.
     *
     * @since 0.1.0
     */
    ORDERED(STEP_MASK_ORDER | STATUS_MASK_START | STATUS_MASK_SUCCESS),
    /**
     * 确认订单过程中发生异常，订单没有确认.
     *
     * @since 0.1.0
     */
    ORDER_EXCEPTION(STEP_MASK_ORDER | STATUS_MASK_START | STATUS_MASK_EXCEPTION),
    /**
     * 确认订单失败.
     *
     * @since 0.1.0
     */
    ORDER_FAIL(STEP_MASK_ORDER | STATUS_MASK_START | STATUS_MASK_FAIL),
    /**
     * 付款阶段.
     * <p>以异步通知消息为准.</p>
     *
     * @since 0.1.0
     */
    PAY(STEP_MASK_PAY),
    /**
     * 正在确认付款.
     *
     * @since 0.1.0
     */
    PAYING(STEP_MASK_PAY | STATUS_MASK_START),
    /**
     * 已经确认付款.
     *
     * @since 0.1.0
     */
    PAYED(STEP_MASK_PAY | STATUS_MASK_START | STATUS_MASK_SUCCESS),
    /**
     * 确认支付过程中发生异常.
     *
     * @since 0.1.0
     */
    PAY_EXCEPTION(STEP_MASK_PAY | STATUS_MASK_START | STATUS_MASK_EXCEPTION),
    /**
     * 确认支付失败.
     *
     * @since 0.1.0
     */
    PAY_FAIL(STEP_MASK_PAY | STATUS_MASK_START | STATUS_MASK_FAIL),
    /**
     * 挂号阶段.
     *
     * @since 0.1.0
     */
    REGISTER(STEP_MASK_REG),
    /**
     * 正在挂号.
     *
     * @since 0.1.0
     */
    REGISTERING(STEP_MASK_REG | STATUS_MASK_START),
    /**
     * 挂号成功.
     *
     * @since 0.1.0
     */
    REGISTERED(STEP_MASK_REG | STATUS_MASK_START | STATUS_MASK_SUCCESS),
    /**
     * 挂号过程中发生异常，挂号失败.
     *
     * @since 0.1.0
     */
    REG_EXCEPTION(STEP_MASK_REG | STATUS_MASK_START | STATUS_MASK_EXCEPTION),
    /**
     * 挂号失败.
     *
     * @since 0.1.0
     */
    REG_FAIL(STEP_MASK_REG | STATUS_MASK_START | STATUS_MASK_FAIL),


    /**
     * 诊间缴费阶段.
     *
     * @since 0.1.0
     */
    RECIPE_PAY(STEP_MASK_RECIPE_PAY),
    /**
     * 正在诊间缴费.
     *
     * @since 0.1.0
     */
    RECIPE_PAYING(STEP_MASK_RECIPE_PAY | STATUS_MASK_START),
    /**
     * 诊间缴费成功.
     *
     * @since 0.1.0
     */
    RECIPE_PAYED(STEP_MASK_RECIPE_PAY | STATUS_MASK_START | STATUS_MASK_SUCCESS),
    /**
     * 诊间缴费过程中发生异常，挂号失败.
     *
     * @since 0.1.0
     */
    RECIPE_PAY_EXCEPTION(STEP_MASK_RECIPE_PAY | STATUS_MASK_START | STATUS_MASK_EXCEPTION),
    /**
     * 诊间缴费失败.
     *
     * @since 0.1.0
     */
    RECIPE_PAY_FAIL(STEP_MASK_RECIPE_PAY | STATUS_MASK_START | STATUS_MASK_FAIL),


    /**
     * 退款阶段.
     *
     * @since 0.1.0
     */
    REFUND(STEP_MASK_REFUND),
    /**
     * 正在退款.
     *
     * @since 0.1.0
     */
    REFUNDING(STEP_MASK_REFUND | STATUS_MASK_START),
    /**
     * 退款成功.
     *
     * @since 0.1.0
     */
    REFUNDED(STEP_MASK_REFUND | STATUS_MASK_START | STATUS_MASK_SUCCESS),
    /**
     * 退款过程中发生异常，退款失败.
     *
     * @since 0.1.0
     */
    REFUND_EXCEPTION(STEP_MASK_REFUND | STATUS_MASK_START | STATUS_MASK_EXCEPTION),
    /**
     * 退款失败.
     *
     * @since 0.1.0
     */
    REFUND_FAIL(STEP_MASK_REFUND | STATUS_MASK_START),
    /**
     * 挂号未获得正常应答，不清楚HIS执行挂号操作的结果.
     *
     * @since 0.1.0
     */
    UNKNOWN(STEP_MASK_UNKNOWN_AND_RETRY),
    /**
     * 重新获取HIS挂号结果.
     *
     * @since 0.1.0
     */
    TRY(STEP_MASK_UNKNOWN_AND_RETRY | STATUS_MASK_START),
    /**
     * 已经从HIS获得挂号结果.
     *
     * @since 0.1.0
     */
    TRY_SUCCESS(STEP_MASK_UNKNOWN_AND_RETRY | STATUS_MASK_START | STATUS_MASK_SUCCESS),
    /**
     * 重试过程又发生异常.
     *
     * @since 0.1.0
     */
    TRY_EXCEPTION(STEP_MASK_UNKNOWN_AND_RETRY | STATUS_MASK_START | STATUS_MASK_EXCEPTION),
    /**
     * 重试仍然没有获得挂号结果.
     *
     * @since 0.1.0
     */
    TRY_FAIL(STEP_MASK_UNKNOWN_AND_RETRY | STATUS_MASK_START),
    /**
     * 重试发现挂号已经成功.
     *
     * @since 0.1.0
     */
    KNOWN_SUCCESS(STEP_MASK_KNOWN_SUCCESS | STATUS_MASK_SUCCESS);

    /**
     * 阶段标志.
     *
     * @since 0.1.0
     */
    private int step;

    /**
     * PayStepEnum的构造函数.
     *
     * @param step 阶段.
     * @since 0.1.0
     */
    PayStepEnum(int step) {
        this.step = step;
    }

    /**
     * 获得支付阶段.
     *
     * @return 支付阶段.
     * @since 0.1.0
     */
    public int step() {
        return step;
    }

    /**
     * 根据阶段获得对应的 {@link PayStepEnum}实例.
     *
     * @param step
     * @return
     * @since 0.1.0
     */
    public PayStepEnum parse(int step) {
        PayStepEnum res = null;
        for (PayStepEnum cur : PayStepEnum.values()) {
            if (cur.step == step) {
                res = cur;
                break;
            }
        }
        return res;
    }
}
