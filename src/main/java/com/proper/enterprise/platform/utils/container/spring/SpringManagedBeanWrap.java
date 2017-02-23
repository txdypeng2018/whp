package com.proper.enterprise.platform.utils.container.spring;

import com.proper.enterprise.platform.utils.container.BeanContainer;

public class SpringManagedBeanWrap<K, M, C extends BeanContainer<K>>
        extends AbstractSpringManagedBean<K, C> {

    /**
     * 被包装实例.
     *
     * @since 0.1.0
     */
    private M wrappered;

    /**
     * 获得被包装实例.
     *
     * @return 被包装实例.
     * @since 0.1.0
     */
    public M getWrappered() {
        return wrappered;
    }

    /**
     * 设置被包装实例.
     *
     * @param wrappered 被包装实例.
     * @since 0.1.0
     */
    public void setWrappered(M wrappered) {
        this.wrappered = wrappered;
    }

}
