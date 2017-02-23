package com.proper.enterprise.isj.payment.converter;

/**
 * 转换对象类型.
 *
 * @param <S> 源对象类型.
 * @param <T> 目标类型对象.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface Converter<S, T> {

    /**
     * 转换方法.
     *
     * @param source 源对象.
     * @param target 目标对象.
     *               <p>
     *               可以为空。为空时，调用{@link #generateTarget()}获得返回对象;不为空时,target参数对象就是返回值.
     *               </p>
     * @return 转换后输出对象.
     * @since 0.1.0
     */
    T convert(S source, T target);

}
