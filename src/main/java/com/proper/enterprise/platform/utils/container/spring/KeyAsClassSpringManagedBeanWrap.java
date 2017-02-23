package com.proper.enterprise.platform.utils.container.spring;

import com.proper.enterprise.platform.utils.container.BeanContainer;

@SuppressWarnings("rawtypes")
public class KeyAsClassSpringManagedBeanWrap<
        M,
        C extends BeanContainer<Class>
        >
        extends SpringManagedBeanWrap<Class, M, C> {

    /**
     * 为被包装实例设置key值，值为类名为clzName参数值的{@link Class}对象.
     * <p>
     * 可能抛出{@link RuntimeException}异常.
     * </p>
     *
     * @param clzName 类名.
     * @since 0.1.0
     */
    public void setKeyClassName(String clzName) {
        try {
            this.setKey(Class.forName(clzName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
