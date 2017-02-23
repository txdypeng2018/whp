package com.proper.enterprise.platform.utils.container.spring;

import com.proper.enterprise.platform.utils.container.AbstractManagedBean;
import com.proper.enterprise.platform.utils.container.BeanContainer;

public class AbstractSpringManagedBean<K, C extends BeanContainer<K>>
        extends AbstractManagedBean<K> implements SpringManagedBean<K> {

    /**
     * 转换器管理器.
     */
    private C manager;

    /**
     * 获得转换器管理器.
     *
     * @return 转换器管理器.
     * @since 0.1.0
     */
    public C getManager() {
        return manager;
    }

    /**
     * 设置转换器管理器.
     *
     * @param manager 转换器管理器.
     * @since 0.1.0
     */
    public void setManager(C manager) {
        this.manager = manager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.getManager().register(this);
    }


}
