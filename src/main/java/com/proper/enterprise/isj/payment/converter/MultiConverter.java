package com.proper.enterprise.isj.payment.converter;

/**
 * 为一个源对象生成一个输出目标对象，并在此对象上执行多个转换方法.
 *
 * @param <T> 输出对象类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class MultiConverter<T> extends AbstractGroupConverter<Object, T, Object, T> implements Converter<Object, T> {

    @Override
    public T convert(Object source, T target) {
        T res = target;
        for (Converter<Object, T> conv : getConvs()) {
            res = conv.convert(source, res);
        }
        return res;
    }

}
