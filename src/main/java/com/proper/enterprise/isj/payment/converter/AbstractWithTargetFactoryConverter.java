package com.proper.enterprise.isj.payment.converter;

import com.proper.enterprise.platform.core.factory.FactoryWrapper;

/**
 * 定义了转换目标工厂的抽象转换器.
 *
 * @param <S> 源对象类型.
 * @param <T> 目标类型对象.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public abstract class AbstractWithTargetFactoryConverter<S, T> extends FactoryWrapper implements Converter<S, T> {

    @Override
    public T convert(S source, T target) {
        T res = target == null ? create() : target;
        if (source != null) {
            doConvert(source, res);
        }
        return res;
    }

    /**
     * 执行转换过程.
     *
     * @param source 转换源对象.
     * @param target 转换目标对象.
     *            <p>
     *            此参数为非空.
     *            </p>
     * @since 0.1.0
     */
    protected abstract void doConvert(S source, T target);

}
