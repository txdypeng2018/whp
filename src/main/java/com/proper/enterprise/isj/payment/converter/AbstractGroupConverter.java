package com.proper.enterprise.isj.payment.converter;

import java.util.Collection;

/**
 * 抽象组合转换器.
 *
 * @param <S> 组合转换器整体源对象类型.
 * @param <T> 组合转换器整体目标对象类型.
 * @param <M> 转换器集合源对象.
 * @param <N> 转换器集合目标对象.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public abstract class AbstractGroupConverter<S, T, M, N> implements Converter<S, T> {

    /**
     * 转换器集合.
     *
     * @since 0.1.0
     */
    private Collection<Converter<M, N>> convs;

    /**
     * 获得转换器集合.
     *
     * @return 转换器集合.
     * @since 0.1.0
     */
    public Collection<Converter<M, N>> getConvs() {
        return convs;
    }

    /**
     * 设置转换器集合.
     *
     * @param convs 转换器集合.
     * @since 0.1.0
     */
    public void setConvs(Collection<Converter<M, N>> convs) {
        this.convs = convs;
    }

}
