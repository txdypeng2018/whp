package com.proper.enterprise.isj.payment.logger;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 日志操作过程中获取Spring的应用上下文的Helper.
 * <p>
 * 日志是通过logback实现的，没有办法直接在自定义Appender中注入 Spring上下文中的Bean.因此需要此Helper获得上下文，然后从中获得
 * Bean实例.<br/>
 * Helper只能获得应用级上下文。因为当前的日志操作的记录方式是异步 记录，所以没有必要获得WebApplicationContext.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
@Service
@Lazy(false)
public class LoggerApplicationContextHelper implements ApplicationContextAware {

    /**
     * 保存应用上下文的属性.
     *
     * @since 0.1.0
     */
    private ApplicationContext context;

    private static final LoggerApplicationContextHelper HELPER = new LoggerApplicationContextHelper();

    /**
     * 获得延时退款日志对象的日志名.
     * <p>
     * 该值定义在conf/isj/isj.properties中；<br/>
     * logback.xml中定义了同名 日志对象。
     * </p>
     *
     * @since 0.1.0
     */
    public String delayLoggerName = "delayRefundLogger";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        HELPER.context = applicationContext;
    }

    /**
     * 获得应用上下文.
     *
     * @return 应用上下文.
     * @since 0.1.0
     */
    public static ApplicationContext findAppContext() {
        return HELPER.context;
    }

    /**
     * 获得延时退款日志对象的名称.
     *
     * @return 延时退款日志对象的名称.
     * @since 0.1.0
     */
    public static String getDelayLoggerName() {
        return HELPER.delayLoggerName;
    }

}
