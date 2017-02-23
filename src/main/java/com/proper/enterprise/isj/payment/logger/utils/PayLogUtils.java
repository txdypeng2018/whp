package com.proper.enterprise.isj.payment.logger.utils;

import static com.proper.enterprise.isj.payment.logger.PayLog.DEFAULT_CAUSE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.proper.enterprise.isj.payment.logger.LoggerApplicationContextHelper;
import com.proper.enterprise.isj.payment.logger.PayLog;
import com.proper.enterprise.isj.payment.logger.PayStepEnum;
import com.proper.enterprise.isj.payment.logger.utils.codec.PayLogEncoder;

/**
 * 支付日志工具类.
 * <p>
 * 支付日志的调用接口,推荐使用此工具类记录日志.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public final class PayLogUtils {

    public static final int MASK_CAUSE_FUNC = 0xFFFF0000;
    public static final int MASK_CAUSE_TYPE = 0x0000FF00;
    public static final int MASK_CAUSE_NUMBER = 0x000000FF;
    public static final int CAUSE_TYPE_START = 0x00000100;
    public static final int CAUSE_TYPE_EXCUTE = 0x00000200;
    public static final int CAUSE_TYPE_SUCCESS = 0x00002000;
    public static final int CAUSE_TYPE_FAIL = 0x00004000;
    public static final int CAUSE_TYPE_EXCEPTION = 0x00008000;

    /**
     * 用于记录交易日志的日志对象.
     *
     * @since 0.1.0
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerApplicationContextHelper.getDelayLoggerName());

    /**
     * 记录交易日志的方法.
     *
     * @param <T>      业务对象的类型.
     * @param step     交易阶段.
     *                 <p>
     *                 参见：{@link PayStepEnum}和{@link PayLogConstrants}。
     *                 </p>
     * @param business 业务对象.
     * @since 0.1.0
     */
    @SuppressWarnings("unchecked")
    public static <T> void log(PayStepEnum step, T business, int logPos) {
        if (business != null) {
            ApplicationContext ctx = LoggerApplicationContextHelper.findAppContext();
            if (LOGGER.isInfoEnabled()) {
                PayLog<T> paylog = ctx.getBean(PayLog.class);
                paylog.setStep(step.step());
                paylog.setCause(logPos);
                paylog.setBusinessObject(business);
                PayLogEncoder<T, ? super PayLog<T>> encoder = ctx.getBean(PayLogEncoder.class);
                String msg = encoder.encode(paylog);
                LOGGER.info(msg);
            }
        }
    }

    /**
     * 记录交易日志的方法.
     * <p>
     * 日志原因参数为{@link DEFAULT_CAUSE}。
     * </p>
     *
     * @param <T>      业务对象的类型.
     * @param step     交易阶段.
     *                 <p>
     *                 参见：{@link PayStepEnum}和{@link PayLogConstrants}。
     *                 </p>
     * @param business 业务对象.
     * @see #log(PayStepEnum, Object, int)
     * @since 0.1.0
     */
    public static <T> void log(PayStepEnum step, T business) {
        log(step, business, DEFAULT_CAUSE);
    }

}
