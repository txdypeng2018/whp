package com.proper.enterprise.isj.payment.converter;

import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.payment.logger.LoggerApplicationContextHelper;
import com.proper.enterprise.isj.payment.logger.PayLog;
import com.proper.enterprise.platform.core.api.IFactory;

/**
 * 默认{@link PayLog}工厂实现类.
 * <p>
 * 从应用上下文中获得{@link PayLog}的实例.
 * </p>
 *
 * @param <T> PayLog实例中业务对象的类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @see DefaultPayLog.
 * @since 0.1.0
 */
@Component
public class DefaultPayLogFactory<T>
        implements IFactory {

    @SuppressWarnings("unchecked")
    @Override
    public PayLog<T> create(Object... objects) {
        return LoggerApplicationContextHelper.findAppContext().getBean(PayLog.class);
    }

    public PayLog<T> newInstance() {
        return create();
    }

}