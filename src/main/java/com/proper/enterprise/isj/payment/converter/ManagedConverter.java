package com.proper.enterprise.isj.payment.converter;

import com.proper.enterprise.platform.utils.container.ManagedBean;

/**
 * 受管转换器接口.
 * <p>
 * 受管转换器是被{@link ConverterManager}管理的转换器.{@link ConverterManager}
 * 通过查找与{@link #getKey()}的值匹配的受管转换器，并创建和返回实例。
 * </p>
 *
 * @param <K> 转换器注册关键字类型.
 * @param <S> 转换源对象类型.
 * @param <T> 输出目标对象类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface ManagedConverter<K, S, T> extends Converter<S, T>, ManagedBean<K> {

    /**
     * 设置受管转换器的Key值.
     *
     * @param key 受管转换器的Key值.
     * @since 0.1.0
     */
    void setKey(K key);
}
