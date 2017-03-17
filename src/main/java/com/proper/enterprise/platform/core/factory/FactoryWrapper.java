package com.proper.enterprise.platform.core.factory;

import com.proper.enterprise.platform.core.api.IFactory;

/**
 * 转换器输出对象的工厂类的包装类.
 *
 * @param <T> 输出对象的类型.
 * @author 王东石<wangdongshi@propersoft.cn>
 * @since 0.1.0
 */
public class FactoryWrapper implements IFactory {

    /**
     * 被包装工厂类.
     *
     * @since 0.1.0
     */
    private IFactory targetFactory;

    /**
     * 获得被包装对象.
     *
     * @return 被包装对象.
     * @since 0.1.0
     */
    public IFactory getTargetFactory() {
        return targetFactory;
    }

    /**
     * 设置被包装对象.
     *
     * @param targetFactory 被包装对象.
     * @since 0.1.0
     */
    public void setTargetFactory(IFactory targetFactory) {
        this.targetFactory = targetFactory;
    }

    @Override
    public <T> T create(Object... objects) {
        return targetFactory == null ? null : targetFactory.create();
    }

}
