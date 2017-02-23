package com.proper.enterprise.isj.payment.logger;

/**
 * 支付日志信息接口.
 * <p>
 * 支付日志信息类是调用记录日志方法
 * {@linkplain com.proper.enterprise.isj.payment.logger.utils.PayLogUtils#log(PayStepEnum, Object)
 * PayLogUtils#log(PayStepEnum, Object)}后,
 * 日志记录过程中，保存信息的对象的接口。写入日志时，可以从该接口的对象获得支付的相关信息。
 * </p>
 *
 * @param <T> 要记录的支付业务对象的类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface PayLog<T> {

    /**
     * 默认日志原因代码.
     */
    public static final int DEFAULT_CAUSE = 0x00;

    /**
     * 获得支付业务的阶段.
     *
     * @return 支付阶段.
     * @see PayStepEnum
     * @see PayLogConstrants
     * @since 0.1.0
     */
    int getStep();

    /**
     * 设置支付业务的阶段.
     *
     * @param step 支付阶段.
     * @see PayStepEnum
     * @see PayLogConstrants
     * @since 0.1.0
     */
    void setStep(int step);

    /**
     * 获得业务对象.
     *
     * @return 业务对象.
     * @since 0.1.0
     */
    T getBusinessObject();

    /**
     * 设置业务对象.
     *
     * @param bo 业务对象.
     * @since 0.1.0
     */
    void setBusinessObject(T bo);

    /**
     * 获得业务对象的字面值.
     * <p>
     * 根据业务需要，可以以json，或base64编码，或其他格式对业务对象进行编码.
     * </p>
     *
     * @return 业务对象的字面值.
     * @since 0.1.0
     */
    String businessObject2String();

    /**
     * 获得业务对象的类型.
     *
     * @return 业务对象类型.
     * @since 0.1.0
     */
    String getBusinessObjectType();

    /**
     * 获得记录日志原因.
     * <p>
     * {@link #getStep()}属性是用来标识业务状态的;{@link #getCause()}是用来标识记录日志的原因。<br/>
     * 比如：不同原因引起的业务失败，可以用此字段标识;不同的异常也可以用此字段标识.
     * </p>
     *
     * @return 获得记录日志原因.
     */
    int getCause();

    /**
     * 设置记录日志原因.
     *
     * @param pos 记录日志原因.
     * @see #getCause()
     */
    void setCause(int pos);
}
