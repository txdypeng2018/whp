package com.proper.enterprise.isj.payment.converter;

import org.springframework.beans.factory.InitializingBean;

/**
 * 在Spring IoC容器中管理的受管转换器接口.
 *
 * @param <K> 转换器注册关键字类型.
 * @param <S> 转换源对象类型.
 * @param <T> 输出目标对象类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @see InitializingBean
 * @since 0.1.0
 */
public interface SpringManagedConverter<K, S, T> extends ManagedConverter<K, S, T>, InitializingBean {

}
