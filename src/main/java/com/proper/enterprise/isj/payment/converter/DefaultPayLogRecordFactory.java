package com.proper.enterprise.isj.payment.converter;

import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.payment.logger.LoggerApplicationContextHelper;
import com.proper.enterprise.isj.payment.logger.entity.DefaultPayLogRecordEntity;
import com.proper.enterprise.isj.payment.logger.entity.PayLogRecord;
import com.proper.enterprise.platform.core.api.IFactory;

/**
 * 默认{@link PayLogRecord}工厂实现类.
 * <p>
 * 从应用上下文中获得{@link PayLogRecord}的实例.
 * </p>
 *
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
@Component
public class DefaultPayLogRecordFactory implements IFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Object... objects) {
        return (T)LoggerApplicationContextHelper.findAppContext().getBean(DefaultPayLogRecordEntity.class);
    }

}
