package com.proper.enterprise.isj.payment.logger.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.proper.enterprise.isj.payment.logger.LoggerApplicationContextHelper;
import com.proper.enterprise.isj.payment.logger.service.PayLoggerService;
import org.springframework.context.ApplicationContext;

/**
 * 支付相关日志.
 * <p>
 * 此Appender是在LogBack.xml中配置的，所以用Spring的自动注入注解无效.
 * 因此需要使用{@link LoggerApplicationContextHelper#findAppContext()}获得上下文. <br/>
 * 使用时，应该保证调用此类对应的日志对象记录日志时，至少要有一个Controller已经被加载过。即尽量在
 * Controller所在线程中调用此类.<br/>
 * 定时任务中可能无法使用此日志。
 * </p>
 *
 * @param <E> 日志信息类型.
 *            <p>
 *            此类型是LogBack中日志消息的子类型.
 *            </p>
 * @author 王东石.
 * @see LoggerApplicationContextHelper
 * @since 0.1.0
 */
public class PayLogAppender<E extends LoggingEvent> extends UnsynchronizedAppenderBase<E> {

    /**
     * 记录日志.
     *
     * @since 0.1.0
     */
    @Override
    protected void append(E eventObject) {
        ApplicationContext ctx = LoggerApplicationContextHelper.findAppContext();
        PayLoggerService service = ctx.getBean(PayLoggerService.class);
        service.savePayLog(eventObject);
    }

}
