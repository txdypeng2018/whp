package com.proper.enterprise.isj.payment.logger.utils.codec;

/**
 * 解码器中创建返回对象所用的工厂类.
 *
 * @param <T> 解码器返回类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public interface Factory<T> {
    /**
     * 工厂方法.
     * <p>
     * 获得解码器返回的对象实例.
     * </p>
     *
     * @return 解码器返回的对象实例.
     * @since 0.1.0
     */
    T newInstance();
}