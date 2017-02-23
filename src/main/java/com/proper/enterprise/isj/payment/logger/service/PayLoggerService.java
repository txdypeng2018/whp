package com.proper.enterprise.isj.payment.logger.service;

import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * 支付日志的服务类.
 *
 * @author 王东石
 * @since 0.1.0
 */
public interface PayLoggerService {

    /**
     * 记录支付相关日志.
     *
     * @param <E>         日志消息类型.
     *                    <p>
     *                    LogBack中的日志消息类型.
     *                    </p>
     * @param eventObject 日志对象.
     * @since 0.1.0
     */
    <E extends LoggingEvent> void savePayLog(E eventObject);

}
