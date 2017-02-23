package com.proper.enterprise.platform.utils.factory;

/**
 * 转换器输出对象的工厂类.
 *
 * @param <T> 输出对象的类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface Factory<T> {

    /**
     * 获得输出对象实例.
     *
     * @return 输出对象实例.
     */
    T create(Object... params);

}
