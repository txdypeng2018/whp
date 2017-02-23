package com.proper.enterprise.isj.payment.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.payment.logger.entity.PayLogRecord;
import com.proper.enterprise.platform.utils.container.BeanContainer;

/**
 * 业务对象转换器.
 *
 * @param <P> 支付日志记录类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
@Service
@SuppressWarnings("rawtypes")
public class BizObjectConverter<P extends PayLogRecord> implements Converter<Object, P> {

    /**
     * 转换器管理类实例.
     *
     * @since 0.1.0
     */
    @Autowired
    @Qualifier("convManager")
    BeanContainer<Class> manager;

    public BeanContainer<Class> getManager() {
        return manager;
    }

    public void setManager(BeanContainer<Class> manager) {
        this.manager = manager;
    }


    /**
     * 根据转换源对象的类型，从{@link #manager}中获得对应的转换器,并执行转换.
     */
    @SuppressWarnings("unchecked")
    @Override
    public P convert(Object source, P target) {
        return (P) ((ManagedConverter<Class, Object, P>) (manager.findBean(source.getClass()))).convert(source, target);
    }

}
