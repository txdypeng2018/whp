package com.proper.enterprise.isj.payment.converter;

import com.proper.enterprise.platform.utils.container.AbstractManagedBean;

/**
 * 
 * @since 0.1.0
 * @author 王东石<wangdongshi@propersoft.cn>
 *
 * @param <K>
 *            转换器注册关键字类型.
 * @param <S>
 *            转换源对象类型.
 * @param <T>
 *            输出目标对象类型.
 */
public abstract class AbstractManagedConverter<K, S, T> extends AbstractManagedBean<K> implements ManagedConverter<K, S, T> {


}
