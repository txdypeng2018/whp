package com.proper.enterprise.isj.payment.logger.entity;

/**
 * 基本日志信息.
 * <p>
 * 与具体业务无关的日志信息.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface LogBase {
    /**
     * 日志上下文创建时间.
     *
     * @return 日志上下文创建时间.
     * @since 0.1.0
     */
    String getLogCtxBirthTm();

    /**
     * 日志上下文创建时间.
     *
     * @param logCtxBirthTm 日志上下文创建时间.
     * @since 0.1.0
     */
    void setLogCtxBirthTm(String logCtxBirthTm);

    /**
     * 调用日志接口的时间戳.
     *
     * @return 调用日志接口的时间戳.
     * @since 0.1.0
     */
    String getLogTm();

    /**
     * 调用日志接口的时间戳.
     *
     * @param logTm 调用日志接口的时间戳.
     * @since 0.1.0
     */
    void setLogTm(String logTm);

    /**
     * 写入日志的时间戳.
     *
     * @return 写入日志的时间戳.
     * @since 0.1.0
     */
    String getWriteTm();

    /**
     * 写入日志的时间戳.
     *
     * @param writeTm 写入日志的时间戳.
     * @since 0.1.0
     */
    void setWriteTm(String writeTm);

    /**
     * 调用日志时业务所处的县城.
     *
     * @return 调用日志时业务所处的县城.
     * @since 0.1.0
     */
    String getThread();

    void setThread(String thread);
}
