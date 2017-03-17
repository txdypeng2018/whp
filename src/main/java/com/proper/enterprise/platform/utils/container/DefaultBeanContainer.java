package com.proper.enterprise.platform.utils.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanContainer<K> implements BeanContainer<K> {

    /**
     * .
     *
     * @since 0.1.0
     */
    private Map<K, ManagedBean<K>> beanMap = new ConcurrentHashMap<>();

    @Override
    public void register(ManagedBean<K> conv) {
        beanMap.put(conv.getKey(), conv);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B extends ManagedBean<K>> B findBean(K key) {
        return (B) beanMap.get(key);
    }

}
